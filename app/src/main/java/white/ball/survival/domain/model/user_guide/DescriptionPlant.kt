package white.ball.survival.domain.model.user_guide

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.Effect

enum class DescriptionPlant(
    val namePlant: Int,
    val imageIdRes: Int,
    val nutritionValue: Int,
    val moistureValue: Int,
    val inNameLocation: Int,
    val effects: List<Effect>
) {
    ALOE(R.string.aloe_item, R.drawable.item_aloe, Item.ALOE.nutritionalValue, Item.ALOE.moistureValue, R.string.desert, Item.ALOE.ability),
    BERRY(R.string.berry_item, R.drawable.item_berry, Item.BERRY.nutritionalValue, Item.BERRY.moistureValue, R.string.locations_berry, Item.BERRY.ability),
    BLUEBERRY(R.string.blueberry_item, R.drawable.item_blueberry, Item.BLUEBERRY.nutritionalValue, Item.BLUEBERRY.moistureValue, R.string.glacier, Item.BLUEBERRY.ability),
    CACTUS(R.string.cactus_item, R.drawable.item_cactus, Item.CACTUS.nutritionalValue, Item.CACTUS.moistureValue, R.string.desert, Item.CACTUS.ability),
    FRUIT(R.string.fruit_item, R.drawable.item_fruit, Item.FRUIT.nutritionalValue, Item.FRUIT.moistureValue, R.string.locations_fruit_item, Item.FRUIT.ability),
    HONEY(R.string.honey_item, R.drawable.item_honey, Item.FRUIT.nutritionalValue, Item.FRUIT.moistureValue, R.string.swamp, Item.HONEY.ability),
    MOSS(R.string.moss_item, R.drawable.item_moss, Item.MOSS.nutritionalValue, Item.MOSS.moistureValue, R.string.locations_moss, Item.MOSS.ability),
    MUSHROOM(R.string.mushroom_raw_item, R.drawable.item_mushroom_raw, Item.MUSHROOM_RAW.nutritionalValue, Item.MUSHROOM_RAW.moistureValue, R.string.swamp, Item.MUSHROOM_RAW.ability),

}