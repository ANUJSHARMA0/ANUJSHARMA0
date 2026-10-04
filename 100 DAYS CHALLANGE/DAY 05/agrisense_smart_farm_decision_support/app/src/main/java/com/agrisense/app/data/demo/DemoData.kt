package com.agrisense.app.data.demo

import com.agrisense.app.domain.model.*

/**
 * DemoData — All demonstration data extracted from the Stitch prototype.
 * This serves as the single source of truth for the prototype's mock data.
 * In a production app, this would be replaced by API calls / Room database.
 */
object DemoData {

    // ── Farm ──
    val farm = Farm()  // Uses default values from data class

    // ── Soil ──
    val soil = SoilData()  // Uses default values from data class

    // ── Weather ──
    val weather = WeatherData(
        dailyForecasts = listOf(
            DayForecast("Mon", "Oct 6", 31.0, 24.0, 0.0, "Sunny", "sunny"),
            DayForecast("Tue", "Oct 7", 30.0, 23.0, 2.0, "Partly Cloudy", "partly_cloudy_day"),
            DayForecast("Wed", "Oct 8", 29.0, 22.0, 8.0, "Light Rain", "rainy"),
            DayForecast("Thu", "Oct 9", 28.0, 22.0, 12.0, "Rain", "rainy"),
            DayForecast("Fri", "Oct 10", 29.0, 23.0, 5.0, "Cloudy", "cloud"),
            DayForecast("Sat", "Oct 11", 30.0, 24.0, 0.0, "Sunny", "sunny"),
            DayForecast("Sun", "Oct 12", 31.0, 24.0, 0.0, "Sunny", "sunny")
        )
    )

    // ── Market Prices ──
    val marketPrices = listOf(
        MarketPrice("Maize", "Surajpur Mandi", 2150.0, 75.0),
        MarketPrice("Mustard", "Surajpur Mandi", 5400.0, 120.0),
        MarketPrice("Chickpea", "Dadri Mandi", 5100.0, -45.0),
        MarketPrice("Paddy Rice", "Noida Mandi", 2280.0, 30.0),
        MarketPrice("Wheat", "Surajpur Mandi", 2275.0, 15.0)
    )

    // ── Crop Definitions ──
    val crops = listOf(
        CropDefinition(
            id = "maize",
            name = "MAIZE",
            variety = "Hybrid Kharif",
            emoji = "🌽",
            icon = "grain",
            season = Season.KHARIF,
            phRange = 5.5..7.5,
            waterRequirement = WaterLevel.MEDIUM,
            waterMm = 500,
            tempRangeC = 21.0..30.0,
            rainfallRangeMm = 500..800,
            durationDays = 95..110,
            costPerAcreRange = 12000..16000,
            yieldTonnesPerHa = 3.5..5.0,
            basePricePerQuintal = 2150,
            soilNitrogenImpactKg = -35,
            riskFactors = listOf(
                "Watch market moisture discount",
                "Requires timely monsoon",
                "Pest pressure in humid conditions"
            ),
            positiveTraits = listOf(
                "High yield potential in loamy soil",
                "Strong local feed demand",
                "Short duration for quick turnaround"
            ),
            description = "Kharif late-cycle feed crop with high yield potential"
        ),
        CropDefinition(
            id = "mustard",
            name = "MUSTARD",
            variety = "Early Sowing (Pusa Bold)",
            emoji = "🌿",
            icon = "eco",
            season = Season.RABI,
            phRange = 6.0..7.5,
            waterRequirement = WaterLevel.LOW,
            waterMm = 250,
            tempRangeC = 15.0..25.0,
            rainfallRangeMm = 250..400,
            durationDays = 110..140,
            costPerAcreRange = 8000..10000,
            yieldTonnesPerHa = 1.2..1.8,
            basePricePerQuintal = 5400,
            soilNitrogenImpactKg = 0,
            riskFactors = listOf(
                "Aphid infestation risk in warm winters",
                "Price volatility in oil markets"
            ),
            positiveTraits = listOf(
                "Resilient to below-average rainfall",
                "Strong mandi demand across local APMCs",
                "Very low water requirement",
                "High oil content premium"
            ),
            description = "Pusa Bold high-oil variety, excellent water efficiency"
        ),
        CropDefinition(
            id = "chickpea",
            name = "CHICKPEA",
            variety = "Desi Chana (JG 11)",
            emoji = "🌱",
            icon = "spa",
            season = Season.RABI,
            phRange = 6.0..8.0,
            waterRequirement = WaterLevel.LOW,
            waterMm = 200,
            tempRangeC = 15.0..30.0,
            rainfallRangeMm = 200..350,
            durationDays = 100..120,
            costPerAcreRange = 7000..9000,
            yieldTonnesPerHa = 1.0..1.5,
            basePricePerQuintal = 5100,
            soilNitrogenImpactKg = 40,
            riskFactors = listOf(
                "Fusarium wilt risk in waterlogged conditions",
                "Frost sensitivity during flowering"
            ),
            positiveTraits = listOf(
                "Fixes atmospheric nitrogen (+40 kg N/ha)",
                "Ideal natural remedy for low-N soil profile",
                "Very low water requirement",
                "Excellent for crop rotation"
            ),
            description = "Chana (JG 11) — soil-healing legume with nitrogen fixation"
        ),
        CropDefinition(
            id = "paddy_rice",
            name = "PADDY RICE",
            variety = "Basmati 1509",
            emoji = "🌾",
            icon = "grass",
            season = Season.KHARIF,
            phRange = 5.5..7.0,
            waterRequirement = WaterLevel.HIGH,
            waterMm = 1200,
            tempRangeC = 22.0..32.0,
            rainfallRangeMm = 1000..1500,
            durationDays = 130..150,
            costPerAcreRange = 14000..18000,
            yieldTonnesPerHa = 2.5..4.0,
            basePricePerQuintal = 2280,
            soilNitrogenImpactKg = -20,
            riskFactors = listOf(
                "Water deficit modeled — may require 3× tubewell pumping",
                "High diesel cost for irrigation",
                "Canal schedule may not suffice"
            ),
            positiveTraits = listOf(
                "Premium Basmati commands export price",
                "Established farming practices locally"
            ),
            description = "Basmati 1509 spread variety — resource intensive but premium pricing"
        )
    )

    // ── Illustrative scenario inputs; displayed outcomes are calculated by the simulator ──
    val scenarioPresets = listOf(
        ScenarioPreset(
            name = "Drought Stress (-30% Rain)",
            description = "High heatwave shock, strict water rations",
            icon = "sunny",
            params = WhatIfParams(
                rainfallPct = -30,
                temperatureShiftC = 2.5,
                marketPricePct = 4,
                fertilizerCostPct = 15,
                waterAvailability = WaterLevel.LOW
            )
        ),
        ScenarioPreset(
            name = "Baseline Normal Conditions",
            description = "Normal demo weather and market assumptions",
            icon = "balance",
            params = WhatIfParams(
                rainfallPct = 0,
                temperatureShiftC = 0.0,
                marketPricePct = 0,
                fertilizerCostPct = 0,
                waterAvailability = WaterLevel.MEDIUM
            )
        ),
        ScenarioPreset(
            name = "Bullish Mandi (+20% Price)",
            description = "Higher demo selling price with stable weather assumptions",
            icon = "trending_up",
            params = WhatIfParams(
                rainfallPct = 10,
                temperatureShiftC = 0.0,
                marketPricePct = 20,
                fertilizerCostPct = -5,
                waterAvailability = WaterLevel.HIGH
            )
        )
    )

    // ── Alerts ──
    val alerts = listOf(
        AlertItem(
            id = "alert_1",
            title = "Nitrogen Advisory",
            description = "Soil nitrogen is low (185 kg/ha). Consider green manuring with Dhaincha or pulse rotation before final bed preparation to build biology.",
            severity = "Urgent",
            actionLabel = "View Fertilizer Schedule",
            timestamp = "2 hours ago",
            icon = "warning"
        ),
        AlertItem(
            id = "alert_2",
            title = "Weather Risk • June–July",
            description = "Modeled rainfall in June–July is 14% below normal. High-water intake crops will require 2 extra rounds of auxiliary tube-well irrigation.",
            severity = "Climate",
            actionLabel = "Check Irrigation Budget",
            timestamp = "5 hours ago",
            icon = "cloud"
        ),
        AlertItem(
            id = "alert_3",
            title = "Agronomic Opportunity",
            description = "Current 29°C temperature and alluvial loam profile strongly favor short-duration Maize, Mustard, and Desi Chickpea with high profit margins.",
            severity = "Favorable",
            actionLabel = "Explore Varietal Seeds",
            timestamp = "1 day ago",
            icon = "lightbulb"
        ),
        AlertItem(
            id = "alert_4",
            title = "Demo Sensor Node A1",
            description = "Illustrative soil moisture reading for Plot 4B: 42%. No physical sensor is connected.",
            severity = "Info",
            actionLabel = "View Sensor Data",
            timestamp = "3 days ago",
            icon = "sensors"
        )
    )

    // ── Sensor Readings ──
    val sensorReadings = listOf(
        SensorReading("node_a1", "Soil Moisture Sensor", "soil_moisture", 42.0, "%", "Active", "2 min ago", "Plot 4B Center"),
        SensorReading("node_a2", "Soil Temperature Probe", "soil_temperature", 24.6, "°C", "Active", "5 min ago", "Plot 4B East"),
        SensorReading("node_a3", "Air Temperature Sensor", "air_temperature", 29.0, "°C", "Active", "5 min ago", "Plot 4B East"),
        SensorReading("node_a4", "Humidity Sensor", "humidity", 72.0, "%", "Active", "5 min ago", "Plot 4B East"),
        SensorReading("node_b1", "Soil pH Probe", "soil_ph", 7.4, " pH", "Warning", "1 hr ago", "Plot 4B West"),
        SensorReading("node_b2", "Rain Gauge", "rainfall", 2.4, "mm", "Active", "30 min ago", "Field Edge North")
    )

    // ── Farm History ──
    val farmHistory = listOf(
        FarmHistoryEntry("Rabi", 2024, "Wheat (HD-2967)", 3.2, 42000, "Good yield, timely harvest"),
        FarmHistoryEntry("Kharif", 2024, "Maize (Hybrid)", 4.1, 58000, "Above average monsoon helped"),
        FarmHistoryEntry("Rabi", 2023, "Mustard (Pusa Bold)", 1.4, 48000, "High oil premium at mandi"),
        FarmHistoryEntry("Kharif", 2023, "Paddy Rice (1121)", 3.0, 35000, "Water shortage, diesel cost high"),
        FarmHistoryEntry("Rabi", 2022, "Chickpea (JG 11)", 1.2, 38000, "Soil nitrogen improved significantly")
    )

    // ── Onboarding Slides ──
    data class OnboardingSlide(
        val step: String,
        val title: String,
        val description: String,
        val metric: String,
        val precision: String
    )

    val onboardingSlides = listOf(
        OnboardingSlide(
            step = "STEP 1 OF 3",
            title = "Know Your Soil & Water",
            description = "Explore example soil and water readings for a Greater Noida demonstration farm.",
            metric = "Demo profile: pH 7.4   Nitrogen: 185 kg/ha",
            precision = "Demo Data"
        ),
        OnboardingSlide(
            step = "STEP 2 OF 3",
            title = "Smart Crop Recommendations",
            description = "Transparent crop suitability scoring based on demo soil, weather, water access, and market assumptions.",
            metric = "4 crops modeled   Confidence estimates shown",
            precision = "Modeled"
        ),
        OnboardingSlide(
            step = "STEP 3 OF 3",
            title = "Simulate & Plan Ahead",
            description = "Explore drought and price scenarios and compare a modeled crop rotation plan.",
            metric = "3 example scenarios   3-year planning horizon",
            precision = "Illustrative"
        )
    )
}
