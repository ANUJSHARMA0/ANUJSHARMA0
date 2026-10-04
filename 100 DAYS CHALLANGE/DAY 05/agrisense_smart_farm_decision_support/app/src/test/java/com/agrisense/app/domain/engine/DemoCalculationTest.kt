package com.agrisense.app.domain.engine

import com.agrisense.app.data.demo.DemoData
import com.agrisense.app.domain.model.Farm
import com.agrisense.app.domain.model.FarmingGoal
import com.agrisense.app.domain.model.NutrientLevel
import com.agrisense.app.domain.model.WaterLevel
import com.agrisense.app.domain.model.WhatIfParams
import com.agrisense.app.ui.viewmodels.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoCalculationTest {
    private val farm = DemoData.farm
    private val soil = DemoData.soil
    private val weather = DemoData.weather
    private val simulator = WhatIfSimulator()

    @Test
    fun recommendationsContainAllDemoCropsWithBoundedScoresAndProfitRanges() {
        val recommendations = RecommendationEngine()
            .generateRecommendations(farm, soil, weather, DemoData.crops)

        assertEquals(setOf("maize", "mustard", "chickpea", "paddy_rice"), recommendations.map {
            it.crop.id
        }.toSet())
        assertTrue(recommendations.zipWithNext().all {
            it.first.suitabilityScore >= it.second.suitabilityScore
        })
        recommendations.forEach { recommendation ->
            assertTrue(recommendation.suitabilityScore in 0..100)
            assertTrue(recommendation.expectedProfitRange.first <= recommendation.expectedProfitRange.last)
            assertTrue(recommendation.modelConfidence in 0..100)
        }
    }

    @Test
    fun profitRangeUsesAreaYieldPriceAndCultivationCost() {
        val crop = DemoData.crops.first { it.id == "maize" }
        val estimate = FarmEconomics.estimate(farm, crop)
        val largerFarm = FarmEconomics.estimate(farm.copy(areaAcres = 5.0), crop)

        assertTrue(estimate.expectedProfitRange.first < estimate.expectedProfitRange.last)
        assertTrue(largerFarm.expectedProfitRange.first > estimate.expectedProfitRange.first)
        assertTrue(largerFarm.expectedProfitRange.last > estimate.expectedProfitRange.last)
        assertTrue(estimate.expectedRevenueRange.first > 0)
        assertTrue(estimate.cultivationCostRange.first > 0)
        assertEquals(
            ((crop.yieldTonnesPerHa.start * (farm.areaAcres / 2.47) *
                crop.basePricePerQuintal * 10 - crop.costPerAcreRange.last * farm.areaAcres) /
                1_000).toInt() * 1_000,
            estimate.expectedProfitRange.first
        )
    }

    @Test
    fun whatIfChangesYieldProfitAndRiskForEnvironmentalAndWaterInputs() {
        val maize = DemoData.crops.first { it.id == "maize" }
        val baseline = simulator.simulate(farm, maize, soil, weather, WhatIfParams())
        val drought = simulator.simulate(
            farm,
            maize,
            soil,
            weather,
            WhatIfParams(
                rainfallPct = -30,
                temperatureShiftC = 2.5,
                waterAvailability = WaterLevel.LOW
            )
        )

        assertTrue(drought.estimatedYieldTHa < baseline.estimatedYieldTHa)
        assertTrue(drought.estimatedProfit < baseline.estimatedProfit)
        assertTrue(drought.riskLevel.ordinal > baseline.riskLevel.ordinal)
    }

    @Test
    fun priceAndFertilizerScenariosChangeProfitWithoutChangingYield() {
        val maize = DemoData.crops.first { it.id == "maize" }
        val baseline = simulator.simulate(farm, maize, soil, weather, WhatIfParams())
        val higherPrice = simulator.simulate(
            farm, maize, soil, weather, WhatIfParams(marketPricePct = 20)
        )
        val higherFertilizerCost = simulator.simulate(
            farm, maize, soil, weather, WhatIfParams(fertilizerCostPct = 30)
        )

        assertEquals(baseline.estimatedYieldTHa, higherPrice.estimatedYieldTHa, 0.0)
        assertTrue(higherPrice.estimatedProfit > baseline.estimatedProfit)
        assertEquals(baseline.estimatedYieldTHa, higherFertilizerCost.estimatedYieldTHa, 0.0)
        assertTrue(higherFertilizerCost.estimatedProfit < baseline.estimatedProfit)
    }

    @Test
    fun riceWaterShortageIsRiskierThanAssuredWater() {
        val rice = DemoData.crops.first { it.id == "paddy_rice" }
        val lowWater = simulator.simulate(
            farm, rice, soil, weather, WhatIfParams(waterAvailability = WaterLevel.LOW)
        )
        val highWater = simulator.simulate(
            farm, rice, soil, weather, WhatIfParams(waterAvailability = WaterLevel.HIGH)
        )

        assertTrue(lowWater.estimatedYieldTHa < highWater.estimatedYieldTHa)
        assertTrue(lowWater.riskLevel.ordinal > highWater.riskLevel.ordinal)
    }

    @Test
    fun nitrogenFixingCropReceivesSoilHealthGoalPreference() {
        val balancedFarm = farm.copy()
        val soilHealthFarm = farm.copy(
            farmingGoal = FarmingGoal.SOIL_HEALTH
        )
        val engine = RecommendationEngine()
        val balanced = engine.generateRecommendations(balancedFarm, soil, weather, DemoData.crops)
        val soilHealth = engine.generateRecommendations(soilHealthFarm, soil, weather, DemoData.crops)
        val balancedChickpea = balanced.first { it.crop.id == "chickpea" }
        val preferredChickpea = soilHealth.first { it.crop.id == "chickpea" }

        assertEquals(NutrientLevel.LOW, soil.nitrogenLevel)
        assertTrue(preferredChickpea.scoreBreakdown.goalScore > balancedChickpea.scoreBreakdown.goalScore)
    }

    @Test
    fun baselineSimulatorUsesConsistentEstimatedProfitAndYield() {
        val maize = DemoData.crops.first { it.id == "maize" }
        val result = simulator.simulate(farm, maize, soil, weather, WhatIfParams())

        assertEquals(result.baseProfit, result.estimatedProfit)
        assertEquals(
            (result.baseYield * 10).toInt() / 10.0,
            result.estimatedYieldTHa,
            0.0
        )
        assertEquals(0.0, result.profitChangeVsBasePct, 0.0)
    }

    @Test
    fun threeScenariosProduceOrderedCropSpecificEstimates() {
        val maize = DemoData.crops.first { it.id == "maize" }
        val favorable = simulator.simulate(
            farm, maize, soil, weather, WhatIfParams(10, 0.0, 15, -5, WaterLevel.HIGH)
        )
        val normal = simulator.simulate(
            farm, maize, soil, weather, WhatIfParams()
        )
        val unfavorable = simulator.simulate(
            farm, maize, soil, weather, WhatIfParams(-25, 2.0, -10, 15, WaterLevel.LOW)
        )

        assertTrue(favorable.estimatedProfit > normal.estimatedProfit)
        assertTrue(normal.estimatedProfit > unfavorable.estimatedProfit)
        assertTrue(favorable.estimatedYieldTHa > unfavorable.estimatedYieldTHa)
        assertTrue(favorable.riskLevel.ordinal < unfavorable.riskLevel.ordinal)
    }

    @Test
    fun longTermPlanRotatesCropsAndIncludesSoilBuildingRotation() {
        val viewModel = MainViewModel()
        val plan = viewModel.generateLongTermPlan()

        assertEquals(3, plan.size)
        assertEquals(3, plan.map { it.crop.id }.toSet().size)
        assertTrue(plan.any { it.crop.soilNitrogenImpactKg > 0 })
        plan.forEach { year ->
            val range = requireNotNull(
                viewModel.getRecommendationForCrop(year.crop.id)
            ).expectedProfitRange
            assertEquals((range.first + range.last) / 2, year.expectedProfit)
        }
        assertTrue(plan.sumOf { it.waterUsageMm } > 0)
    }

    @Test
    fun cropComparisonSelectionIsLimitedToThree() {
        val viewModel = MainViewModel()

        assertEquals(2, viewModel.selectedCropIds.value.size)
        viewModel.toggleCropSelection("chickpea")
        viewModel.toggleCropSelection("paddy_rice")

        assertEquals(3, viewModel.selectedCropIds.value.size)
    }
}
