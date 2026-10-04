package com.agrisense.app.domain.model

/**
 * AgriSense Domain Models — all data classes for the application.
 * Extracted from the Stitch prototype's hardcoded HTML values.
 */

// ── Enums ──

enum class SoilType(val displayName: String) {
    LOAMY("Loamy"),
    CLAY("Clay"),
    SANDY("Sandy"),
    ALLUVIAL("Alluvial"),
    RED("Red Laterite"),
    BLACK_COTTON("Black Cotton")
}

enum class WaterLevel(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class NutrientLevel(val displayName: String, val emoji: String) {
    LOW("Low", "⚠️"),
    MEDIUM("Medium", "●"),
    HIGH("High", "✓")
}

enum class RiskLevel(val displayName: String, val emoji: String) {
    LOW("Low", "🟢"),
    LOW_MEDIUM("Low-Med", "🟢"),
    MEDIUM("Medium", "🟡"),
    HIGH("High", "🔴")
}

enum class FarmingGoal(val displayName: String, val icon: String) {
    BALANCED("Balanced Goal", "balance"),
    MAX_PROFIT("Maximum Profit", "currency_rupee"),
    LOW_RISK("Low Risk / Safety", "shield"),
    QUICK_INCOME("Quick Turnaround", "schedule"),
    LOW_WATER("Low Water Usage", "water_drop"),
    SOIL_HEALTH("Soil Health", "compost")
}

enum class Season(val displayName: String) {
    KHARIF("Kharif"),
    RABI("Rabi"),
    ZAID("Zaid")
}

// ── Core Data Classes ──

data class Farm(
    val farmerName: String = "Rameshwar",
    val location: String = "Greater Noida, Uttar Pradesh",
    val district: String = "Gautam Buddh Nagar",
    val plotName: String = "Plot 4B Bed",
    val areaAcres: Double = 2.5,
    val soilType: SoilType = SoilType.LOAMY,
    val irrigationType: String = "Canal + Tubewell",
    val waterAvailability: WaterLevel = WaterLevel.MEDIUM,
    val previousCrop: String = "Wheat (Rabi 2024)",
    val farmingGoal: FarmingGoal = FarmingGoal.BALANCED,
    val targetSeason: Season = Season.RABI
)

data class SoilData(
    val ph: Double = 7.4,
    val phStatus: String = "Normal",
    val nitrogenKgHa: Double = 185.0,
    val nitrogenLevel: NutrientLevel = NutrientLevel.LOW,
    val phosphorusKgHa: Double = 22.4,
    val phosphorusLevel: NutrientLevel = NutrientLevel.MEDIUM,
    val potassiumKgHa: Double = 290.0,
    val potassiumLevel: NutrientLevel = NutrientLevel.HIGH,
    val organicCarbonPct: Double = 0.48,
    val ecDsM: Double = 0.38,
    val zincPpm: Double = 0.82,
    val ironPpm: Double = 5.2,
    val lastTestedDate: String = "Sep 2025",
    val soilHealthScore: Int = 68
)

data class WeatherData(
    val currentTempC: Double = 29.0,
    val feelsLikeC: Double = 32.0,
    val humidity: Int = 72,
    val windKmh: Double = 12.0,
    val windDirection: String = "NW",
    val condition: String = "Partly Cloudy",
    val uvIndex: Int = 7,
    val rainfallForecastMm: Double = 18.0,
    val rainfallBaseline: String = "Below 10-yr seasonal baseline",
    val hasDeficitAlert: Boolean = true,
    val dailyForecasts: List<DayForecast> = emptyList()
)

data class DayForecast(
    val day: String,
    val dateLabel: String,
    val tempHighC: Double,
    val tempLowC: Double,
    val rainfallMm: Double,
    val condition: String,
    val icon: String
)

data class MarketPrice(
    val cropName: String,
    val mandiName: String,
    val pricePerQuintal: Double,
    val priceChange: Double, // positive = up
    val unit: String = "₹/q"
)

data class CropDefinition(
    val id: String,
    val name: String,
    val variety: String,
    val emoji: String,
    val icon: String,
    val season: Season,
    val phRange: ClosedFloatingPointRange<Double>,
    val waterRequirement: WaterLevel,
    val waterMm: Int,
    val tempRangeC: ClosedFloatingPointRange<Double>,
    val rainfallRangeMm: IntRange,
    val durationDays: IntRange,
    val costPerAcreRange: IntRange,
    val yieldTonnesPerHa: ClosedFloatingPointRange<Double>,
    val basePricePerQuintal: Int,
    val soilNitrogenImpactKg: Int,
    val riskFactors: List<String>,
    val positiveTraits: List<String>,
    val description: String
)

data class CropRecommendation(
    val crop: CropDefinition,
    val suitabilityScore: Int,
    val financialEstimate: CropFinancialEstimate,
    val expectedProfitRange: IntRange,
    val modelConfidence: Int,
    val riskLevel: RiskLevel,
    val scoreBreakdown: ScoreBreakdown,
    val positiveFactors: List<String>,
    val concerns: List<String>,
    val badge: String // "Top Match", "High Stability", "Soil Rebuilding", "Resource Intensive"
)

data class CropFinancialEstimate(
    val expectedYieldTonnesRange: ClosedFloatingPointRange<Double>,
    val expectedRevenueRange: IntRange,
    val cultivationCostRange: IntRange,
    val expectedProfitRange: IntRange
)

data class ScoreBreakdown(
    val soilScore: Int,
    val weatherScore: Int,
    val waterScore: Int,
    val economicsScore: Int,
    val durationScore: Int,
    val seasonScore: Int,
    val goalScore: Int,
    val riskAdjustment: Int
)

data class WhatIfParams(
    val rainfallPct: Int = 0,
    val temperatureShiftC: Double = 0.0,
    val marketPricePct: Int = 0,
    val fertilizerCostPct: Int = 0,
    val waterAvailability: WaterLevel = WaterLevel.MEDIUM
)

data class WhatIfResult(
    val estimatedYieldTHa: Double,
    val estimatedProfit: Int,
    val profitChangeVsBasePct: Double,
    val riskLevel: RiskLevel,
    val waterStressLevel: String,
    val waterStressPct: Int,
    val waterStressDescription: String,
    val baseProfit: Int,
    val baseYield: Double
)

data class ScenarioPreset(
    val name: String,
    val description: String,
    val icon: String,
    val params: WhatIfParams
)

data class LongTermPlanYear(
    val year: Int,
    val season: Season,
    val crop: CropDefinition,
    val expectedProfit: Int,
    val soilNitrogenChange: Int,
    val waterUsageMm: Int,
    val rationale: String
)

data class SensorReading(
    val sensorId: String,
    val sensorName: String,
    val type: String, // "soil_moisture", "temperature", "humidity", "npk"
    val value: Double,
    val unit: String,
    val status: String, // "Active", "Offline", "Warning"
    val lastUpdated: String,
    val location: String
)

data class AlertItem(
    val id: String,
    val title: String,
    val description: String,
    val severity: String, // "Urgent", "Climate", "Favorable", "Info"
    val actionLabel: String,
    val timestamp: String,
    val icon: String,
    val isRead: Boolean = false
)

data class FarmHistoryEntry(
    val season: String,
    val year: Int,
    val crop: String,
    val yieldTonnes: Double,
    val profitRupees: Int,
    val notes: String
)
