package white.ball.survival.domain.model.location

import white.ball.survival.R
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.BuildRepository


enum class Location(
    val nameLocationId: Int,
    val imageResId: Int,
    val imageIconTravelResId: Int,
    val imageForFightResId: Int,
    val animals: List<Animal>,
    var isPlayerFindHere: Boolean,
    var build: BuildRepository?,

    ) {

    FOREST(R.string.forest, R.drawable.location_forest, R.drawable.icon_forest_travel, R.drawable.location_forest_for_fight,
        mutableListOf(Animal.FRUIT, Animal.MOSPEH, Animal.RABBIT, Animal.NERD, Animal.AQUA, Animal.BOA), true, null),

    ROCK(R.string.rock, R.drawable.location_cave, R.drawable.icon_cave_travel, R.drawable.location_cave_for_fight,
        mutableListOf(Animal.MIGLE, Animal.GAZL, Animal.NURG, Animal.AQUA, Animal.BOA, Animal.FRUIT), false, null),

    DESERT(R.string.desert,R.drawable.location_desert, R.drawable.icon_desert_travel, R.drawable.location_desert_for_fight,
        mutableListOf(Animal.VETON, Animal.BITE, Animal.SPORk, Animal.BOA),false, null),

    SWAMP(R.string.swamp,R.drawable.location_swamp,R.drawable.icon_swamp_travel, R.drawable.location_swamp_for_fight,
        mutableListOf(Animal.SLIME, Animal.ZHABARIB, Animal.PUCHIK, Animal.PCHYOSA, Animal.AQUA, Animal.BOA, Animal.FRUIT),false, null),

    GLACIER(R.string.glacier, R.drawable.location_glacier, R.drawable.icon_glacier_travel, R.drawable.location_glacier_for_fight,
        mutableListOf(Animal.CHUKECH, Animal.WOLFHOUND, Animal.PENGUIN, Animal.AQUA, Animal.BOA),false, null),

    CEMETERY(R.string.cemetery, R.drawable.location_cemetry, R.drawable.icon_cemetry_travel, R.drawable.location_cemetry_for_fight,
        mutableListOf(Animal.SKELETON, Animal.PUK_LUK, Animal.BESFAT, Animal.AQUA, Animal.BOA, Animal.FRUIT), false, null),
}