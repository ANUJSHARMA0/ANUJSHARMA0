package com.agrisense.app.domain.engine

import com.agrisense.app.domain.model.*
import kotlin.math.roundToInt

/**
 * RecommendationEngine — Computes crop suitability scores.
 *
 * Scoring: Soil(25) + Weather/season(22) + Water(18) + Economics(18) + Duration(10) + Goal(7) + Risk(-5..0).
 * All values are demonstration estimates, not production-grade agronomic models.
 */
class RecommendationEngine {

    fun generateRecommendations(
        farm: Farm,
        soil: SoilData,
        weather: WeatherData,
        crops: List<CropDefinition>
    ): List<CropRecommendation> {
        return crops.map { crop ->
            val breakdown = computeBreakdown(farm, soil, weather, crop)
            val totalScore = (breakdown.soilScore + breakdown.weatherScore +
                    breakdown.waterScore + breakdown.economicsScore +
                    breakdown.durationScore + breakdown.seasonScore +
                    breakdown.goalScore + breakdown.riskAdjustment)
                .coerceIn(0, 100)

            val financialEstimate = FarmEconomics.estimate(farm, crop)
            val confidence = computeConfidence(crop, soil, weather)
            val riskLevel = assessRisk(crop, farm, weather)
            val badge = assignBadge(crop, totalScore, riskLevel)

            CropRecommendation(
                crop = crop,
                suitabilityScore = totalScore,
                financialEstimate = financialEstimate,
                expectedProfitRange = financialEstimate.expectedProfitRange,
                modelConfidence = confidence,
                riskLevel = riskLevel,
                scoreBreakdown = breakdown,
                positiveFactors = crop.positiveTraits,
                concerns = crop.riskFactors,
                badge = badge
            )
        }.sortedByDescending { it.suitabilityScore }
    }

    private fun computeBreakdown(
        farm: Farm,
        soil: SoilData,
        weather: WeatherData,
        crop: CropDefinition
    ): ScoreBreakdown {
        // Soil Score (0-25)
        var soilScore = 0
        if (soil.ph in crop.phRange) soilScore += 12
        else if (soil.ph in (crop.phRange.start - 0.5)..(crop.phRange.endInclusive + 0.5)) soilScore += 8
        else soilScore += 4

        // Nitrogen adequacy
        when {
            crop.soilNitrogenImpactKg > 0 -> soilScore += 8  // Nitrogen fixer doesn't need high N
            soil.nitrogenLevel == NutrientLevel.HIGH -> soilScore += 8
            soil.nitrogenLevel == NutrientLevel.MEDIUM -> soilScore += 6
            else -> soilScore += 3  // Low N with heavy feeder
        }

        // Soil type bonus
        if (farm.soilType == SoilType.LOAMY) soilScore += 5 // Loamy is generally good

        // Weather Score (0-17)
        var weatherScore = 0
        if (weather.currentTempC in crop.tempRangeC) weatherScore += 10
        else if (weather.currentTempC in (crop.tempRangeC.start - 3.0)..(crop.tempRangeC.endInclusive + 3.0)) weatherScore += 6
        else weatherScore += 2

        val averageRainfall = (crop.rainfallRangeMm.first + crop.rainfallRangeMm.last) / 2.0
        val averageDurationWeeks = ((crop.durationDays.first + crop.durationDays.last) / 2.0) / 7.0
        val weeklyRainfallNeed = averageRainfall / averageDurationWeeks
        val rainfallRatio = weather.rainfallForecastMm / weeklyRainfallNeed
        weatherScore += when {
            rainfallRatio >= 0.8 -> 7
            rainfallRatio >= 0.5 -> 5
            rainfallRatio >= 0.25 -> 3
            else -> 1
        }
        val seasonScore = if (crop.season == farm.targetSeason) 5 else 1

        // Water Score (0-18)
        val waterScore = when {
            crop.waterRequirement == WaterLevel.LOW -> 18
            crop.waterRequirement == farm.waterAvailability -> 14
            crop.waterRequirement == WaterLevel.MEDIUM && farm.waterAvailability == WaterLevel.HIGH -> 16
            crop.waterRequirement == WaterLevel.HIGH && farm.waterAvailability == WaterLevel.MEDIUM -> 6
            crop.waterRequirement == WaterLevel.HIGH && farm.waterAvailability == WaterLevel.LOW -> 2
            else -> 10
        }

        // Economics Score (0-18)
        val avgCost = (crop.costPerAcreRange.first + crop.costPerAcreRange.last) / 2.0 *
            farm.areaAcres
        val avgRevenue = FarmEconomics.estimatedBaseProfit(farm, crop) + avgCost
        val roi = (avgRevenue - avgCost) / avgCost
        val economicsScore = (roi * 9).coerceIn(0.0, 18.0).roundToInt()

        // Duration Score (0-10)
        val avgDuration = (crop.durationDays.first + crop.durationDays.last) / 2.0
        val durationScore = when {
            avgDuration <= 105 -> 10
            avgDuration <= 120 -> 8
            avgDuration <= 135 -> 6
            else -> 4
        }

        // Farming goal Score (0-7)
        val goalScore = when (farm.farmingGoal) {
            FarmingGoal.BALANCED -> 4
            FarmingGoal.MAX_PROFIT -> (roi * 4).coerceIn(1.0, 7.0).roundToInt()
            FarmingGoal.LOW_RISK -> when (assessRisk(crop, farm, weather)) {
                RiskLevel.LOW -> 7
                RiskLevel.LOW_MEDIUM -> 5
                RiskLevel.MEDIUM -> 3
                RiskLevel.HIGH -> 1
            }
            FarmingGoal.QUICK_INCOME -> when {
                avgDuration <= 105 -> 7
                avgDuration <= 120 -> 5
                avgDuration <= 135 -> 3
                else -> 1
            }
            FarmingGoal.LOW_WATER -> when (crop.waterRequirement) {
                WaterLevel.LOW -> 7
                WaterLevel.MEDIUM -> 4
                WaterLevel.HIGH -> 1
            }
            FarmingGoal.SOIL_HEALTH -> when {
                crop.soilNitrogenImpactKg > 0 -> 7
                crop.soilNitrogenImpactKg == 0 -> 4
                else -> 1
            }
        }

        // Risk Adjustment (-5 to 0)
        var riskAdj = 0
        if (crop.waterRequirement == WaterLevel.HIGH && farm.waterAvailability != WaterLevel.HIGH) riskAdj -= 3
        if (weather.hasDeficitAlert && crop.waterRequirement != WaterLevel.LOW) riskAdj -= 2
        if (crop.season != farm.targetSeason && crop.waterRequirement == WaterLevel.HIGH) riskAdj -= 1

        return ScoreBreakdown(
            soilScore = soilScore.coerceAtMost(25),
            weatherScore = weatherScore.coerceAtMost(17),
            waterScore = waterScore,
            economicsScore = economicsScore,
            durationScore = durationScore,
            seasonScore = seasonScore,
            goalScore = goalScore,
            riskAdjustment = riskAdj
        )
    }

    private fun computeConfidence(crop: CropDefinition, soil: SoilData, weather: WeatherData): Int {
        // Base confidence
        var conf = 70
        if (soil.ph in crop.phRange) conf += 5
        if (weather.currentTempC in crop.tempRangeC) conf += 4
        if (crop.waterRequirement == WaterLevel.LOW) conf += 3
        if (crop.soilNitrogenImpactKg > 0 || soil.nitrogenLevel != NutrientLevel.LOW) conf += 2
        return conf.coerceAtMost(95)
    }

    private fun assessRisk(crop: CropDefinition, farm: Farm, weather: WeatherData): RiskLevel {
        var riskScore = 0
        if (crop.waterRequirement == WaterLevel.HIGH && farm.waterAvailability != WaterLevel.HIGH) riskScore += 3
        if (weather.hasDeficitAlert && crop.waterRequirement != WaterLevel.LOW) riskScore += 2
        if (crop.durationDays.last > 130) riskScore += 1

        return when {
            riskScore >= 4 -> RiskLevel.HIGH
            riskScore >= 3 -> RiskLevel.MEDIUM
            riskScore >= 1 -> RiskLevel.LOW_MEDIUM
            else -> RiskLevel.LOW
        }
    }

    private fun assignBadge(crop: CropDefinition, score: Int, risk: RiskLevel): String {
        return when {
            score >= 85 -> "Top Match"
            crop.soilNitrogenImpactKg > 0 -> "Soil Rebuilding"
            risk == RiskLevel.HIGH -> "Resource Intensive"
            crop.waterRequirement == WaterLevel.LOW -> "High Stability"
            else -> "Good Option"
        }
    }
}
