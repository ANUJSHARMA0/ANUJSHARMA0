package com.agrisense.app.domain.engine

import com.agrisense.app.domain.model.CropDefinition
import com.agrisense.app.domain.model.Farm
import com.agrisense.app.domain.model.NutrientLevel
import com.agrisense.app.domain.model.RiskLevel
import com.agrisense.app.domain.model.SoilData
import com.agrisense.app.domain.model.WaterLevel
import com.agrisense.app.domain.model.WeatherData
import com.agrisense.app.domain.model.WhatIfParams
import com.agrisense.app.domain.model.WhatIfResult
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Transparent demonstration model. Results are estimates, not agronomic predictions.
 */
class WhatIfSimulator {

    fun simulate(
        farm: Farm,
        crop: CropDefinition,
        soil: SoilData,
        weather: WeatherData,
        params: WhatIfParams
    ): WhatIfResult {
        val baseYield = (crop.yieldTonnesPerHa.start + crop.yieldTonnesPerHa.endInclusive) / 2.0
        val baseCost = (crop.costPerAcreRange.first + crop.costPerAcreRange.last) / 2.0 *
            farm.areaAcres
        val soilFactor = when {
            crop.soilNitrogenImpactKg > 0 && soil.nitrogenLevel == NutrientLevel.LOW -> 1.03
            crop.soilNitrogenImpactKg < 0 && soil.nitrogenLevel == NutrientLevel.LOW -> 0.95
            else -> 1.0
        }
        val modeledBaseYield = baseYield * soilFactor
        val baseProfit = FarmEconomics.estimatedBaseProfit(farm, crop) +
            (modeledBaseYield - baseYield) * (farm.areaAcres / ACRES_PER_HECTARE) *
            crop.basePricePerQuintal * QUINTALS_PER_TONNE
        val rainfallFactor = (1.0 + params.rainfallPct * RAINFALL_YIELD_FACTOR)
            .coerceIn(MIN_YIELD_FACTOR, MAX_YIELD_FACTOR)
        val baseTemperatureStress = temperatureStress(weather.currentTempC, crop)
        val adjustedTemperature = weather.currentTempC + params.temperatureShiftC
        val adjustedTemperatureStress = temperatureStress(adjustedTemperature, crop)
        val temperatureFactor = (
            1.0 - (adjustedTemperatureStress - baseTemperatureStress) * TEMPERATURE_YIELD_FACTOR
            ).coerceIn(MIN_YIELD_FACTOR, MAX_YIELD_FACTOR)
        val waterFactor = waterYieldFactor(crop, params.waterAvailability)
        val estimatedYield = (modeledBaseYield * rainfallFactor * temperatureFactor *
            waterFactor).coerceAtLeast(0.1)

        val fertilizerCostFactor = 1.0 + params.fertilizerCostPct / 100.0 *
            FERTILIZER_SHARE_OF_COST
        val pumpingCostFactor = when (params.waterAvailability) {
            WaterLevel.LOW -> LOW_WATER_COST_FACTOR
            WaterLevel.MEDIUM -> 0.0
            WaterLevel.HIGH -> HIGH_WATER_COST_FACTOR
        }
        val cultivationCost = baseCost * (fertilizerCostFactor + pumpingCostFactor)
        val expectedPrice = crop.basePricePerQuintal * (1.0 + params.marketPricePct / 100.0)
        val areaHectares = farm.areaAcres / ACRES_PER_HECTARE
        val estimatedProfit = estimatedYield * areaHectares * expectedPrice *
            QUINTALS_PER_TONNE - cultivationCost

        val waterStress = waterStress(crop, params.waterAvailability, rainfallFactor)
        val riskScore = (if (params.rainfallPct < -10) 2 else 0) +
            (if (adjustedTemperatureStress >= baseTemperatureStress + 1.0) 2 else 0) +
            (if (waterStress >= 2) 2 else 0) +
            (if (params.fertilizerCostPct > 15) 1 else 0) +
            (if (params.marketPricePct < -10) 2 else 0)
        val riskLevel = when {
            riskScore >= 5 -> RiskLevel.HIGH
            riskScore >= 3 -> RiskLevel.MEDIUM
            riskScore >= 1 -> RiskLevel.LOW_MEDIUM
            else -> RiskLevel.LOW
        }
        val stress = when {
            waterStress >= 3 -> Triple(
                "Severe Stress",
                85,
                "Water access and rainfall assumptions create substantial modeled crop stress."
            )
            waterStress >= 1 -> Triple(
                "Moderate Stress",
                55,
                "Monitor field moisture and irrigation timing under this modeled scenario."
            )
            else -> Triple(
                "Lower Stress",
                25,
                "The selected water and rainfall assumptions indicate lower modeled water stress."
            )
        }

        return WhatIfResult(
            estimatedYieldTHa = (estimatedYield * 10).roundToInt() / 10.0,
            estimatedProfit = estimatedProfit.roundToInt(),
            profitChangeVsBasePct = if (baseProfit == 0.0) 0.0 else
                ((estimatedProfit - baseProfit) / abs(baseProfit) * 1000).roundToInt() / 10.0,
            riskLevel = riskLevel,
            waterStressLevel = stress.first,
            waterStressPct = stress.second,
            waterStressDescription = stress.third,
            baseProfit = baseProfit.roundToInt(),
            baseYield = modeledBaseYield
        )
    }

    fun formatRainfallLabel(pct: Int): String {
        val prefix = if (pct > 0) "+" else ""
        val suffix = if (pct >= 0) "Surplus" else "Below Average"
        return "$prefix$pct% $suffix"
    }

    fun formatTemperatureLabel(shift: Double): String {
        val prefix = if (shift > 0) "+" else ""
        val suffix = when {
            shift >= 1.0 -> "Warmer"
            shift <= -1.0 -> "Cooler"
            else -> "Normal"
        }
        return "$prefix${shift}°C $suffix"
    }

    fun formatPriceLabel(pct: Int, crop: CropDefinition): String {
        val computedPrice = (crop.basePricePerQuintal * (1 + pct / 100.0)).roundToInt()
        val prefix = if (pct >= 0) "+" else ""
        return "₹${formatIndianNumber(computedPrice)}/q ($prefix$pct%)"
    }

    fun formatCostLabel(pct: Int): String {
        val prefix = if (pct >= 0) "+" else ""
        return "$prefix$pct% Fertilizer cost"
    }

    fun formatWaterLabel(level: WaterLevel): String = when (level) {
        WaterLevel.LOW -> "Low water availability"
        WaterLevel.MEDIUM -> "Medium water availability"
        WaterLevel.HIGH -> "High water availability"
    }

    private fun temperatureStress(temperatureC: Double, crop: CropDefinition): Double {
        val optimalTemperature = (crop.tempRangeC.start + crop.tempRangeC.endInclusive) / 2.0
        return abs(temperatureC - optimalTemperature)
    }

    private fun waterYieldFactor(crop: CropDefinition, water: WaterLevel): Double =
        when (crop.waterRequirement) {
            WaterLevel.LOW -> when (water) {
                WaterLevel.LOW -> 0.96
                WaterLevel.MEDIUM -> 1.0
                WaterLevel.HIGH -> 1.0
            }
            WaterLevel.MEDIUM -> when (water) {
                WaterLevel.LOW -> 0.84
                WaterLevel.MEDIUM -> 1.0
                WaterLevel.HIGH -> 1.0
            }
            WaterLevel.HIGH -> when (water) {
                WaterLevel.LOW -> 0.62
                WaterLevel.MEDIUM -> 0.82
                WaterLevel.HIGH -> 1.0
            }
        }

    private fun waterStress(crop: CropDefinition, water: WaterLevel, rainfallFactor: Double): Int {
        val accessStress = when (crop.waterRequirement) {
            WaterLevel.LOW -> if (water == WaterLevel.LOW) 1 else 0
            WaterLevel.MEDIUM -> when (water) {
                WaterLevel.LOW -> 2
                WaterLevel.MEDIUM, WaterLevel.HIGH -> 0
            }
            WaterLevel.HIGH -> when (water) {
                WaterLevel.LOW -> 3
                WaterLevel.MEDIUM -> 2
                WaterLevel.HIGH -> 0
            }
        }
        return accessStress + if (rainfallFactor < 0.9) 1 else 0
    }

    private companion object {
        const val ACRES_PER_HECTARE = 2.47
        const val QUINTALS_PER_TONNE = 10
        const val RAINFALL_YIELD_FACTOR = 0.004
        const val TEMPERATURE_YIELD_FACTOR = 0.04
        const val MIN_YIELD_FACTOR = 0.4
        const val MAX_YIELD_FACTOR = 1.4
        const val FERTILIZER_SHARE_OF_COST = 0.4
        const val LOW_WATER_COST_FACTOR = 0.05
        const val HIGH_WATER_COST_FACTOR = 0.08
    }
}

fun formatIndianNumber(number: Int): String {
    val str = number.toString()
    if (str.length <= 3) return str
    val last3 = str.takeLast(3)
    val remaining = str.dropLast(3)
    val grouped = remaining.reversed().chunked(2).joinToString(",").reversed()
    return "$grouped,$last3"
}
