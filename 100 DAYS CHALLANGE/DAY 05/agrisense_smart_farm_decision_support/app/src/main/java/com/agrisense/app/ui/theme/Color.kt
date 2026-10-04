package com.agrisense.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AgriSense color palette — mapped directly from the Stitch DESIGN.md
 * Material 3 color tokens for the agricultural decision-support theme.
 *
 * Primary: Deep Forest Canopy (#1B4332)
 * Secondary: Sprout Green (#006C48 / #52B788)
 * Tertiary: Terracotta Alert (#500C00 / #E76F51)
 */

// ── Primary ──
val Primary = Color(0xFF012D1D)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF1B4332)
val OnPrimaryContainer = Color(0xFF86AF99)
val InversePrimary = Color(0xFFA5D0B9)

// ── Secondary ──
val Secondary = Color(0xFF006C48)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFF92F7C3)
val OnSecondaryContainer = Color(0xFF00734D)

// ── Tertiary ──
val Tertiary = Color(0xFF500C00)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFF741B04)
val OnTertiaryContainer = Color(0xFFFF8364)

// ── Error ──
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

// ── Surface ──
val Surface = Color(0xFFF8F9FF)
val OnSurface = Color(0xFF121C2A)
val SurfaceVariant = Color(0xFFD9E3F6)
val OnSurfaceVariant = Color(0xFF414844)
val SurfaceDim = Color(0xFFD0DBED)
val SurfaceBright = Color(0xFFF8F9FF)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFEFF4FF)
val SurfaceContainer = Color(0xFFE6EEFF)
val SurfaceContainerHigh = Color(0xFFDEE9FC)
val SurfaceContainerHighest = Color(0xFFD9E3F6)
val SurfaceTint = Color(0xFF3F6653)

// ── Inverse ──
val InverseSurface = Color(0xFF27313F)
val InverseOnSurface = Color(0xFFEAF1FF)

// ── Outline ──
val Outline = Color(0xFF717973)
val OutlineVariant = Color(0xFFC1C8C2)

// ── Background ──
val Background = Color(0xFFF8F9FF)
val OnBackground = Color(0xFF121C2A)

// ── Fixed colors (used in component specs) ──
val PrimaryFixed = Color(0xFFC1ECD4)
val PrimaryFixedDim = Color(0xFFA5D0B9)
val OnPrimaryFixed = Color(0xFF002114)
val OnPrimaryFixedVariant = Color(0xFF274E3D)

val SecondaryFixed = Color(0xFF92F7C3)
val SecondaryFixedDim = Color(0xFF75DAA8)
val OnSecondaryFixed = Color(0xFF002113)
val OnSecondaryFixedVariant = Color(0xFF005235)

val TertiaryFixed = Color(0xFFFFDAD2)
val TertiaryFixedDim = Color(0xFFFFB4A2)
val OnTertiaryFixed = Color(0xFF3C0700)
val OnTertiaryFixedVariant = Color(0xFF83260E)

// ── Extended palette for UI (from DESIGN.md semantic mapping) ──
val AgriGreen = Color(0xFF52B788)           // Sprout Green — positive indicators
val AgriForest = Color(0xFF2D6A4F)          // Active button pressed state
val AgriTerracotta = Color(0xFFE76F51)      // Warning/alert color
val AgriAmber = Color(0xFFF4A261)           // Caution yellow
val AgriCharcoal = Color(0xFF1F2937)        // Deep text
val AgriBone = Color(0xFFF8F9FA)            // Warm neutral background
val AgriBorder = Color(0xFFE9EFE7)          // Card border
val AgriNeutralGray = Color(0xFFD1D5DB)     // Input border / off-states

// ── Confidence badge colors ──
val ConfidenceHighBg = Color(0xFFE8F5E9)
val ConfidenceHighText = Color(0xFF1B4332)
val ConfidenceMedBg = Color(0xFFFEF3C7)
val ConfidenceMedText = Color(0xFF92400E)
val ConfidenceLowBg = Color(0xFFFEE2E2)
val ConfidenceLowText = Color(0xFF991B1B)
