---
name: AgriSense Design System
colors:
  surface: '#f8f9ff'
  surface-dim: '#d0dbed'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e6eeff'
  surface-container-high: '#dee9fc'
  surface-container-highest: '#d9e3f6'
  on-surface: '#121c2a'
  on-surface-variant: '#414844'
  inverse-surface: '#27313f'
  inverse-on-surface: '#eaf1ff'
  outline: '#717973'
  outline-variant: '#c1c8c2'
  surface-tint: '#3f6653'
  primary: '#012d1d'
  on-primary: '#ffffff'
  primary-container: '#1b4332'
  on-primary-container: '#86af99'
  inverse-primary: '#a5d0b9'
  secondary: '#006c48'
  on-secondary: '#ffffff'
  secondary-container: '#92f7c3'
  on-secondary-container: '#00734d'
  tertiary: '#500c00'
  on-tertiary: '#ffffff'
  tertiary-container: '#741b04'
  on-tertiary-container: '#ff8364'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c1ecd4'
  primary-fixed-dim: '#a5d0b9'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#274e3d'
  secondary-fixed: '#92f7c3'
  secondary-fixed-dim: '#75daa8'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005235'
  tertiary-fixed: '#ffdad2'
  tertiary-fixed-dim: '#ffb4a2'
  on-tertiary-fixed: '#3c0700'
  on-tertiary-fixed-variant: '#83260e'
  background: '#f8f9ff'
  on-background: '#121c2a'
  surface-variant: '#d9e3f6'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.03em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1rem
  margin-tablet: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system is engineered for rural and semi-urban Indian farmers navigating high-stakes operational choices—ranging from seed selection, fertilizer scheduling, and irrigation cycles to mandi pricing predictions. The visual tone balances modern agricultural science with deep grounded reliability: approachable, pragmatic, reassuring, and completely devoid of cognitive clutter.

The aesthetic fuses Material 3 primitives with an organic, tactile sensibility:
- **Tone:** Grounded, authoritative, optimistic, and direct.
- **Visual Stance:** Modern AgTech. Clean structural cards, generous tap zones for outdoor, single-hand operation under direct sunlight, and glanceable quantitative badges.
- **Experience Mandate:** Every critical recommendation (such as pest outbreaks, sowing windows, and market rates) delivers instant visual clarity through high-contrast status signifiers, bilingual-friendly spatial structures, and tactile feedback.

## Colors

The palette directly reflects living agriculture: deep botanical shade canopy, young foliage growth, earth loam, and sunlit harvests. It adheres strictly to WCAG 2.1 AA/AAA contrast ratios against daylight glare.

### Semantic Mapping
- **Primary (`#1B4332`)**: Forest Canopy. Anchors key interactive buttons, active app bar icons, bottom navigation selections, and dominant headers.
- **Primary Container (`#E8F5E9`) / Secondary Surface (`#F1F4F0`)**: Low-luminance tinted backgrounds that soothe eye fatigue and cleanly delineate card groupings.
- **Secondary (`#52B788`)**: Sprout Green. Highlights positive yields, favorable moisture balances, peak harvest windows, and completed actions.
- **Tertiary (`#E76F51`) & Warning Amber (`#F4A261`)**: Terracotta & Sun Harvest. Denotes high heat alerts, modeled economic risk, water stress warnings, and urgent crop health alerts.
- **Neutral Foreground (`#1F2937`)**: Deep Charcoal Slate. Used for primary typography to avoid harsh pure-black eye strain while guaranteeing crisp readability under outdoor glare.
- **Neutral Surface Tone (`#F8F9FA` to `#E9EFE7`)**: Soft fertile bone tones providing organic warmth compared to clinical sterile whites.

## Typography

**Plus Jakarta Sans** is the single typographic voice across all roles. Its geometric roundness, generous apertures, and tall x-height make it exceptionally legible when rendered at both small sizes and across non-Latin scripts (such as Hindi, Marathi, Telugu, and Punjabi transliterations).

### Hierarchy Implementation Rules
- **Numerical Prominence**: Currency values (₹ INR), quintal metrics, moisture percentages, and yield projections utilize `headline-lg` or `display-mobile` with `700` weight to allow instant field assessment.
- **Reading Comfort**: Body text never drops below 14px (`body-md`) for vital operational instructions or agronomic guidance.
- **Status & Badging**: Metric tags (Confidence, Risk Levels) deploy uppercase or title-case `label-md` with tightened letter spacing to ensure rapid visual parsing without wrapping.

## Layout & Spacing

The layout model is driven by a mobile-first 4-column fluid grid on compact screens (&lt;600dp), stepping to an 8-column layout for tablets and landscape foldable devices.

### Layout Principles
- **Touch Perimeter**: All primary interactive elements adhere to Android's strict 48x48dp minimum physical touch target, with 56dp height prioritized for core decision buttons.
- **Margins & Gutters**: Outer canvas margins sit at `margin` (16dp) on mobile, preventing thumb clipping on curved Android displays, with an internal grid gutter of 12dp to 16dp.
- **Vertical Rhythm**: Built upon a strict 4dp baseline / 8dp component grid. Content groupings stay cohesive using `space-xs` (4dp) and `space-sm` (8dp) internally, while major insight modules separate via `space-md` (16dp) and `space-lg` (24dp).

## Elevation & Depth

Visual hierarchy leverages **Tonal Surface Layering** supported by gentle ambient shadows, eschewing harsh, sharp drop-shadows that appear cluttered in intense sunlight.

### Depth Levels
- **Level 0 (Background Canvas)**: Surface neutral `#F8F9FA`. Completely flat.
- **Level 1 (Default Decision Cards & List Tiles)**: Surface neutral container `#FFFFFF` with a 1px border stroke of `#E9EFE7` and subtle ambient tint: `box-shadow: 0 2px 6px rgba(27, 67, 50, 0.05)`.
- **Level 2 (Active/Selected Card or Weather Alert)**: `#FFFFFF` surface with `box-shadow: 0 4px 12px rgba(27, 67, 50, 0.08)`.
- **Level 3 (Sticky Bottom Action Sheets & Floating Filter Pills)**: `box-shadow: 0 6px 20px rgba(31, 41, 55, 0.12)`.
- **Level 4 (Critical Urgent Modals / Pest Warning Overlays)**: `box-shadow: 0 12px 32px rgba(27, 67, 50, 0.18)`.

## Shapes

The design system establishes a welcoming, organic geometry aligned with Material 3 standard curvatures:
- **Base Surfaces & Interactive Cards**: `16px` to `20px` corner radii (`rounded-lg`), balancing tactile softness with space efficiency.
- **Form Controls & Inputs**: `12px` corner radii for text fields and dropdowns.
- **Buttons, Metric Chips, and Quick Badges**: Full pill caps (`9999px`) or `10px` rounded chips to signal immediate tap affordance and clearly contrast against rectangular content cards.
- **Dialogs & Bottom Sheets**: Top corners rounded to `24px` (`rounded-xl`) to clearly dock sheets to the bottom bezel.

## Components

### Buttons
- **Primary Action (e.g., "Confirm Sowing Plan", "View Mandi Rates")**: Height 56dp. Solid `#1B4332` container, `#FFFFFF` text (`label-lg`), pill-radius (`9999px`) or 16px rounded rect. Active states shift to `#2D6A4F`.
- **Secondary Action**: Height 48dp to 56dp. `#E8F5E9` surface with `#1B4332` text, no border.
- **Outlined / Tertiary Action**: 1.5px stroke in `#2D6A4F`, transparent background, 48dp minimum touch target.

### Decision & Metric Badges
Compact status capsules highlighting key agricultural indicators:
- **Confidence Rating**: Pill badge (`label-md`). High: `#E8F5E9` background with `#1B4332` label; Medium: `#FEF3C7` background with `#92400E` label; Low: `#FEE2E2` background with `#991B1B` label.
- **Expected Profit (₹ INR)**: Prominent emphasis badge using `display-mobile` numerals accompanied by a trending green icon (`#2D6A4F`).
- **Water Requirement & Modeled Risk**: Horizontal indicator bar accompanied by a moisture drop icon and contextual risk pill (`Low Risk` in Sprout Green `#52B788`, `Elevated` in Terracotta `#E76F51`).

### Cards (Farm Decision Modules)
- **Container**: White `#FFFFFF` surface with 16px corner radius, bounded by an earthy hairline border `#E9EFE7`.
- **Padding**: 16dp internal padding (`space-md`).
- **Header**: Top section houses crop/plot identifier and date with a right-aligned confidence chip.
- **Body**: Core projection metrics organized in a clear 2-column or 3-column glanceable grid with muted labels (`body-sm`) over bold values (`headline-sm`).
- **Footer**: Single primary tactile action button or split inline buttons.

### Form Inputs & Selectors
- **Input Fields**: Height 56dp. Background `#F8F9FA` with 12px radius, resting stroke `#D1D5DB`. On focus: 2px `#1B4332` stroke with slight `#E8F5E9` container tint. Large 16px placeholder text prevents Android auto-zoom and eases readability.
- **Dropdowns & Crop Selectors**: Include leading graphical iconography (crop glyphs) to support low-literacy users.

### Checkboxes, Radio Buttons, & Switches
- **Touch Target**: Centered within a 48x48dp tap boundary.
- **Coloration**: Selected state fills with `#1B4332`; toggle switches use `#52B788` for positive active states and `#D1D5DB` for neutral off-states.