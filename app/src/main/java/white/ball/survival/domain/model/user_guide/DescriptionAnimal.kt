package white.ball.survival.domain.model.user_guide

import white.ball.survival.R
import white.ball.survival.domain.model.animal.Animal

enum class DescriptionAnimal(
    val nameAnimal: Int,
    val imageIdRes: Int,
    val inNameLocation: Int,
    val effects: Int
) {

    AQUA(R.string.aqua, R.drawable.animal_aqua, R.string.locations_aqua, Animal.AQUA.effect.nameText),
    BESFAT(R.string.besfat, R.drawable.animal_besfat, R.string.cemetery, Animal.BESFAT.effect.nameText ),
    BITE(R.string.bite, R.drawable.animal_bite, R.string.desert, Animal.BITE.effect.nameText),
    BOA(R.string.boa, R.drawable.animal_boa, R.string.anywhere_locations, Animal.BOA.effect.nameText),
    CHUKECH(R.string.chukech, R.drawable.animal_chukech, R.string.glacier, Animal.CHUKECH.effect.nameText),
    FRUIT(R.string.fruit_animal, R.drawable.animal_fruit, R.string.locations_fruit, Animal.FRUIT.effect.nameText),
    GAZL(R.string.gazl, R.drawable.animal_gazl, R.string.rock, Animal.GAZL.effect.nameText),
    MIGLE(R.string.migle, R.drawable.animal_migle, R.string.rock, Animal.MIGLE.effect.nameText),
    MOSPEH(R.string.mospeh, R.drawable.animal_mospeh, R.string.forest, Animal.MOSPEH.effect.nameText),
    NERD(R.string.nerd, R.drawable.animal_nerd, R.string.forest, Animal.NERD.effect.nameText),
    NURG(R.string.nurg, R.drawable.animal_nurg, R.string.rock, Animal.NURG.effect.nameText),
    PCHYOSA(R.string.pchyosa, R.drawable.animal_pchyosa, R.string.swamp, Animal.PCHYOSA.effect.nameText),
    PENGUIN(R.string.penguin, R.drawable.animal_penguin, R.string.glacier, Animal.PENGUIN.effect.nameText),
    PUCHIK(R.string.puchik, R.drawable.animal_puchik, R.string.swamp, Animal.PUCHIK.effect.nameText),
    PUK_LUK(R.string.puk_luk, R.drawable.animal_puk_luk, R.string.cemetery, Animal.PUK_LUK.effect.nameText),
    RABBIT(R.string.rabbit, R.drawable.animal_rabbit, R.string.forest, Animal.RABBIT.effect.nameText),
    SKELETON(R.string.skeleton, R.drawable.animal_skeleton, R.string.cemetery, Animal.SKELETON.effect.nameText),
    SLIME(R.string.slime, R.drawable.animal_slime, R.string.slime, Animal.SLIME.effect.nameText),
    SPORK(R.string.spork, R.drawable.animal_spork, R.string.desert, Animal.SPORk.effect.nameText),
    VETON(R.string.veton, R.drawable.animal_veton, R.string.desert, Animal.VETON.effect.nameText),
    WOLFHOUND(R.string.wolfhound, R.drawable.animal_wolfhound, R.string.glacier, Animal.WOLFHOUND.effect.nameText),
    ZHABARIB(R.string.zhabarib, R.drawable.animal_zhabarib, R.string.swamp, Animal.ZHABARIB.effect.nameText),
    ENSLAVER(R.string.enslaver, R.drawable.animal_enslaver, R.string.unknown, R.string.unknown)
}