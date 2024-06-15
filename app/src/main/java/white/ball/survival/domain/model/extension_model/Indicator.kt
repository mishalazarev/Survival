package white.ball.survival.domain.model.extension_model

import white.ball.survival.R


data class IndicatorHealth(
    val indicator: Indicator,
    var isPoisoning: Boolean = false,
    var isRegeneration: Boolean = false,
    val nameIndicator: Int = R.string.health
)

data class IndicatorEndurance(
    val indicator: Indicator,
    var isWeakYet: Boolean = false,
    var isRegeneration: Boolean = false,
    val nameIndicator: Int = R.string.endurance
)

data class IndicatorSatiety(
    val indicator: Indicator,
    val nameIndicator: Int = R.string.satiety
)

data class IndicatorThirst(
    val indicator: Indicator,
    val nameIndicator: Int = R.string.thirst
)

data class IndicatorFood(
    val indicator: Indicator,
    val nameIndicator: Int = R.string.food_indicator
)

data class IndicatorWater(
    val indicator: Indicator,
    val nameIndicator: Int = R.string.water_indicator
)

data class Indicator(
    var percent: Int = 100,
    var maxPercent: Int = 100,
) {
    companion object {
        @JvmStatic
        val COLOR_LINE = mapOf(
            (0) to R.color.black,
            (1..19) to R.color.red,
            (20..39) to R.color.orange,
            (40..59) to R.color.yellow,
            (60..79) to R.color.green_light,
            (80..100) to R.color.green
        )
        @JvmStatic
        val COLOR_ABILITY = mapOf(
            Effect.POISONING to R.color.poisoning,
            Effect.WEAKNESS to R.color.weakness,
            Effect.REGENERATION to R.color.regeneration
        )
    }
}
