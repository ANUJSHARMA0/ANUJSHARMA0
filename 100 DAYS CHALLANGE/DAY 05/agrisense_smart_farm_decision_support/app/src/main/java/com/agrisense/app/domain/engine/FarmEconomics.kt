package com.agrisense.app.domain.engine

import com.agrisense.app.domain.model.CropDefinition
import com.agrisense.app.domain.model.CropFinancialEstimate
import com.agrisense.app.domain.model.Farm
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

object FarmEconomics {
    private const val ACRES_PER_HECTARE = 2.47
    private const val QUINTALS_PER_TONNE = 10
    private const val PROFIT_ROUNDING = 1_000.0

    fun estimate(farm: Farm, crop: CropDefinition): CropFinancialEstimate {
        val areaHectares = farm.areaAcres / ACRES_PER_HECTARE
        val lowYield = crop.yieldTonnesPerHa.start * areaHectares
        val highYield = crop.yieldTonnesPerHa.endInclusive * areaHectares
        val highestCultivationCost = crop.costPerAcreRange.last * farm.areaAcres
        val lowestCultivationCost = crop.costPerAcreRange.first * farm.areaAcres
        val lowestRevenue = lowYield * crop.basePricePerQuintal * QUINTALS_PER_TONNE
        val highestRevenue = highYield * crop.basePricePerQuintal * QUINTALS_PER_TONNE
        val profitRange = floorToThousand(lowestRevenue - highestCultivationCost)..
            ceilToThousand(highestRevenue - lowestCultivationCost)

        return CropFinancialEstimate(
            expectedYieldTonnesRange = lowYield..highYield,
            expectedRevenueRange = floorToThousand(lowestRevenue)..
                ceilToThousand(highestRevenue),
            cultivationCostRange = floorToThousand(lowestCultivationCost)..
                ceilToThousand(highestCultivationCost),
            expectedProfitRange = profitRange
        )
    }

    fun estimatedBaseProfit(farm: Farm, crop: CropDefinition): Double {
        val areaHectares = farm.areaAcres / ACRES_PER_HECTARE
        val expectedYield = (crop.yieldTonnesPerHa.start + crop.yieldTonnesPerHa.endInclusive) / 2.0
        val expectedPrice = crop.basePricePerQuintal.toDouble()
        val expectedCultivationCost =
            (crop.costPerAcreRange.first + crop.costPerAcreRange.last) / 2.0 * farm.areaAcres

        return expectedYield * areaHectares * expectedPrice * QUINTALS_PER_TONNE -
            expectedCultivationCost
    }

    private fun floorToThousand(amount: Double): Int =
        (floor(amount / PROFIT_ROUNDING) * PROFIT_ROUNDING).coerceAtLeast(0.0).roundToInt()

    private fun ceilToThousand(amount: Double): Int =
        (ceil(amount / PROFIT_ROUNDING) * PROFIT_ROUNDING).coerceAtLeast(0.0).roundToInt()
}
