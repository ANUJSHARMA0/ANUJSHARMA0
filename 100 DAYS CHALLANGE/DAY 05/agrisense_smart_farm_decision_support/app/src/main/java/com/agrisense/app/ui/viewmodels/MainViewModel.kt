package com.agrisense.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.agrisense.app.data.demo.DemoData
import com.agrisense.app.domain.engine.RecommendationEngine
import com.agrisense.app.domain.engine.WhatIfSimulator
import com.agrisense.app.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * MainViewModel — Central state holder for the AgriSense prototype.
 * Manages farm data, recommendations, What-If simulation, and long-term planning.
 *
 * In production, this would be split into per-feature ViewModels with proper DI.
 * Consolidated here for prototype simplicity.
 */
class MainViewModel : ViewModel() {

    private val recommendationEngine = RecommendationEngine()
    private val whatIfSimulator = WhatIfSimulator()

    // ── Farm State ──
    private val _farm = MutableStateFlow(DemoData.farm)
    val farm: StateFlow<Farm> = _farm.asStateFlow()

    // ── Soil State ──
    private val _soil = MutableStateFlow(DemoData.soil)
    val soil: StateFlow<SoilData> = _soil.asStateFlow()

    // ── Weather State ──
    private val _weather = MutableStateFlow(DemoData.weather)
    val weather: StateFlow<WeatherData> = _weather.asStateFlow()

    // ── Market State ──
    val marketPrices: List<MarketPrice> = DemoData.marketPrices
    val crops: List<CropDefinition> = DemoData.crops

    // ── Crop Recommendations ──
    private val _recommendations = MutableStateFlow<List<CropRecommendation>>(emptyList())
    val recommendations: StateFlow<List<CropRecommendation>> = _recommendations.asStateFlow()

    // ── Selected crops for comparison ──
    private val _selectedCropIds = MutableStateFlow<Set<String>>(setOf("maize", "mustard"))
    val selectedCropIds: StateFlow<Set<String>> = _selectedCropIds.asStateFlow()

    private val _simulationCropId = MutableStateFlow("maize")
    val simulationCropId: StateFlow<String> = _simulationCropId.asStateFlow()

    // ── What-If State ──
    private val _whatIfParams = MutableStateFlow(
        WhatIfParams(waterAvailability = DemoData.farm.waterAvailability)
    )
    val whatIfParams: StateFlow<WhatIfParams> = _whatIfParams.asStateFlow()

    private val _whatIfResult = MutableStateFlow<WhatIfResult?>(null)
    val whatIfResult: StateFlow<WhatIfResult?> = _whatIfResult.asStateFlow()

    // ── Scenario Presets ──
    val scenarioPresets: List<ScenarioPreset> = DemoData.scenarioPresets

    // ── Alerts ──
    val alerts: List<AlertItem> = DemoData.alerts

    // ── Sensors ──
    val sensorReadings: List<SensorReading> = DemoData.sensorReadings

    // ── Farm History ──
    val farmHistory: List<FarmHistoryEntry> = DemoData.farmHistory

    // ── Onboarding ──
    val onboardingSlides = DemoData.onboardingSlides

    init {
        generateRecommendations()
        runWhatIfSimulation()
    }

    // ── Farm Setup ──
    fun updateFarm(farm: Farm) {
        _farm.value = farm
        generateRecommendations()
        runWhatIfSimulation()
    }

    // ── Recommendations ──
    fun generateRecommendations() {
        val results = recommendationEngine.generateRecommendations(
            farm = _farm.value,
            soil = _soil.value,
            weather = _weather.value,
            crops = DemoData.crops
        )
        _recommendations.value = results
    }

    fun getCropById(cropId: String): CropDefinition? {
        return DemoData.crops.find { it.id == cropId }
    }

    fun getRecommendationForCrop(cropId: String): CropRecommendation? {
        return _recommendations.value.find { it.crop.id == cropId }
    }

    fun selectSimulationCrop(cropId: String) {
        require(DemoData.crops.any { it.id == cropId }) { "Unknown demo crop: $cropId" }
        _simulationCropId.value = cropId
        runWhatIfSimulation()
    }

    fun simulateForCrop(cropId: String, params: WhatIfParams): WhatIfResult {
        val crop = getCropById(cropId)
            ?: throw IllegalArgumentException("Unknown demo crop: $cropId")
        return whatIfSimulator.simulate(
            farm = _farm.value,
            crop = crop,
            soil = _soil.value,
            weather = _weather.value,
            params = params
        )
    }

    // ── Comparison ──
    fun toggleCropSelection(cropId: String) {
        val current = _selectedCropIds.value.toMutableSet()
        if (current.contains(cropId)) {
            if (current.size > 1) current.remove(cropId) // Keep at least 1
        } else {
            if (current.size < 3) current.add(cropId) // Max 3
        }
        _selectedCropIds.value = current
    }

    fun getSelectedRecommendations(): List<CropRecommendation> {
        return _recommendations.value.filter { it.crop.id in _selectedCropIds.value }
    }

    // ── What-If Simulation ──
    fun updateWhatIfParams(params: WhatIfParams) {
        _whatIfParams.value = params
        runWhatIfSimulation()
    }

    fun updateRainfall(pct: Int) {
        _whatIfParams.value = _whatIfParams.value.copy(rainfallPct = pct)
        runWhatIfSimulation()
    }

    fun updateTemperature(shift: Double) {
        _whatIfParams.value = _whatIfParams.value.copy(temperatureShiftC = shift)
        runWhatIfSimulation()
    }

    fun updateMarketPrice(pct: Int) {
        _whatIfParams.value = _whatIfParams.value.copy(marketPricePct = pct)
        runWhatIfSimulation()
    }

    fun updateFertilizerCost(pct: Int) {
        _whatIfParams.value = _whatIfParams.value.copy(fertilizerCostPct = pct)
        runWhatIfSimulation()
    }

    fun updateWaterAvailability(level: WaterLevel) {
        _whatIfParams.value = _whatIfParams.value.copy(waterAvailability = level)
        runWhatIfSimulation()
    }

    fun applyPreset(preset: ScenarioPreset) {
        _whatIfParams.value = preset.params
        runWhatIfSimulation()
    }

    fun resetSimulation() {
        _whatIfParams.value = WhatIfParams(waterAvailability = _farm.value.waterAvailability)
        runWhatIfSimulation()
    }

    private fun runWhatIfSimulation() {
        _whatIfResult.value = simulateForCrop(
            cropId = _simulationCropId.value,
            params = _whatIfParams.value
        )
    }

    // ── Long-Term Plan ──
    fun generateLongTermPlan(): List<LongTermPlanYear> {
        val rankedCrops = _recommendations.value
        if (rankedCrops.isEmpty()) return emptyList()

        val plan = mutableListOf<LongTermPlanYear>()
        val primaryCrop = rankedCrops.first().crop
        val soilBuildingCrop = rankedCrops.firstOrNull {
            it.crop.soilNitrogenImpactKg > 0 && it.crop.id != primaryCrop.id
        }?.crop
        val secondYearCrop = soilBuildingCrop
            ?: rankedCrops.firstOrNull { it.crop.id != primaryCrop.id }?.crop
            ?: primaryCrop
        val thirdYearCrop = rankedCrops.firstOrNull {
            it.crop.id != primaryCrop.id && it.crop.id != secondYearCrop.id
        }?.crop ?: primaryCrop
        val rotationOrder = listOf(
            primaryCrop,
            secondYearCrop,
            thirdYearCrop
        )

        var cumulativeN = _soil.value.nitrogenKgHa.toInt()

        for (i in 0..2) {
            val crop = rotationOrder[i]
            val rec = _recommendations.value.find { it.crop.id == crop.id }
            val avgProfit = rec?.expectedProfitRange?.let { (it.first + it.last) / 2 } ?: 40000
            cumulativeN += crop.soilNitrogenImpactKg

            val rationale = when {
                crop.soilNitrogenImpactKg > 0 -> "Nitrogen-fixing rotation to restore soil health (${crop.soilNitrogenImpactKg} kg N/ha)"
                crop.waterRequirement == WaterLevel.LOW -> "Low water requirement suits forecasted deficit conditions"
                else -> "Highest suitability score for current farm conditions"
            }

            plan.add(
                LongTermPlanYear(
                    year = i + 1,
                    season = crop.season,
                    crop = crop,
                    expectedProfit = avgProfit,
                    soilNitrogenChange = crop.soilNitrogenImpactKg,
                    waterUsageMm = crop.waterMm,
                    rationale = rationale
                )
            )
        }
        return plan
    }

    // ── Formatting helpers ──
    val simulator get() = whatIfSimulator
}
