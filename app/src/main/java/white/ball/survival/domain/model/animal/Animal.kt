package white.ball.survival.domain.model.animal

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.*

enum class Animal(
    val nameAnimal: Int,
    val imageId: Int,

    var health: IndicatorHealth,
    var endurance: IndicatorEndurance,
    val damage: Int,

    var effect: Effect,
    var effectHaveDeny: MutableList<Effect>,

    var drop: List<Item>,
    val chanceEscapePlayer: EscapeType
) {

    AQUA(
        R.string.aqua, R.drawable.animal_aqua, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)),10, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.LARGE_FLASK_WITH_WATER), EscapeType.SURRENDER
    ),
    BESFAT(
        R.string.besfat, R.drawable.animal_besfat, IndicatorHealth(Indicator(100,100)),
        IndicatorEndurance(Indicator(100,100)),20, Effect.NO_EFFECT, mutableListOf(),
        mutableListOf(Item.PHOSPHORUS_BRIGHT, Item.FLASK_WITH_WATER), EscapeType.SURRENDER
    ),
    BITE(
        R.string.bite, R.drawable.animal_bite, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)),20, Effect.POISONING, mutableListOf(),
        mutableListOf(Item.CACTUS, Item.ALOE, Item.FRUIT, Item.BERRY, Item.FLASK_WITH_WATER), EscapeType.SURRENDER
    ),
    BOA(
        R.string.boa, R.drawable.animal_boa, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)),20, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.SCROLL), EscapeType.SURRENDER
    ),
    CHUKECH(
        R.string.chukech, R.drawable.animal_chukech, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 30,Effect.STUN,
        mutableListOf(), mutableListOf(Item.WOOD, Item.FISH_RAW, Item.MOSS, Item.BLUEBERRY),
        EscapeType.SURRENDER
    ),
    ENSLAVER(
        R.string.enslaver, R.drawable.animal_enslaver, IndicatorHealth(Indicator(500,500)),
        IndicatorEndurance(Indicator(0,0)), 40,Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(), EscapeType.NO_SURRENDER
    ),
    FRUIT(
        R.string.fruit_animal, R.drawable.animal_fruit, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(150, 150)),20, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.FRUIT, Item.FLASK_WITH_WATER, Item.SEED_IN_SHELL), EscapeType.SURRENDER
    ),
    GAZL(
        R.string.gazl, R.drawable.animal_gazl, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 20, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.MEAT_RAW, Item.STONE), EscapeType.NO_SURRENDER
    ),
    MIGLE(
        R.string.migle, R.drawable.animal_migle, IndicatorHealth(Indicator(200, 200)),
        IndicatorEndurance(Indicator(200, 200)), 20, Effect.STUN,
        mutableListOf(), mutableListOf(Item.STONE), EscapeType.SURRENDER
    ),
    MOSPEH(
        R.string.mospeh, R.drawable.animal_mospeh, IndicatorHealth(Indicator(100,100)),
        IndicatorEndurance(Indicator(100,100)), 20, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.WOOD, Item.FISH_RAW, Item.LEAVES), EscapeType.SURRENDER
    ),
    NERD(
        R.string.nerd, R.drawable.animal_nerd, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 10, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.WOOD, Item.LEAVES), EscapeType.SURRENDER
    ),
    NURG(
        R.string.nurg, R.drawable.animal_nurg, IndicatorHealth(Indicator(200, 200)),
        IndicatorEndurance(Indicator(200, 200)), 30, Effect.WEAKNESS,
        mutableListOf(), mutableListOf(Item.NURG_PLATE),
        EscapeType.NO_SURRENDER
    ),
    PCHYOSA(
        R.string.pchyosa, R.drawable.animal_pchyosa, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)), 30, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.BERRY, Item.HONEY), EscapeType.NO_SURRENDER
    ),
    PENGUIN(
        R.string.penguin, R.drawable.animal_penguin, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)),10, Effect.STUN,
        mutableListOf(), mutableListOf(Item.MEAT_RAW, Item.FISH_RAW),
        EscapeType.NO_SURRENDER
    ),
    PUCHIK(
        R.string.puchik, R.drawable.animal_puchik, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 20, Effect.WEAKNESS,
        mutableListOf(), mutableListOf(Item.PUCHICK_PLATE),
        EscapeType.SURRENDER
    ),
    PUK_LUK(
        R.string.puk_luk, R.drawable.animal_puk_luk, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 30, Effect.POISONING,
        mutableListOf(), mutableListOf(Item.WEB, Item.POISONED_POINT),
        EscapeType.NO_SURRENDER
    ),
    RABBIT(
        R.string.rabbit, R.drawable.animal_rabbit, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)), 10,Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.MEAT_RAW), EscapeType.SURRENDER
    ),
    SKELETON(
        R.string.skeleton, R.drawable.animal_skeleton, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 20,Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.BERRY), EscapeType.NO_SURRENDER
    ),
    SLIME(
        R.string.slime, R.drawable.animal_slime, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)), 10, Effect.POISONING,
        mutableListOf(), mutableListOf(Item.BERRY, Item.FRUIT, Item.FISH_RAW),
        EscapeType.NO_SURRENDER
    ),
    SPORk(
        R.string.spork, R.drawable.animal_spork, IndicatorHealth(Indicator(200, 200)),
        IndicatorEndurance(Indicator(200, 200)),30, Effect.POISONING,
        mutableListOf(), mutableListOf(Item.SPORK_PLATE),
        EscapeType.NO_SURRENDER
    ),
    VETON(
        R.string.veton, R.drawable.animal_veton, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 10, Effect.POISONING,
        mutableListOf(), mutableListOf(Item.POISONED_POINT),
        EscapeType.NO_SURRENDER
    ),
    WOLFHOUND(
        R.string.wolfhound, R.drawable.animal_wolfhound, IndicatorHealth(Indicator(100, 100)),
        IndicatorEndurance(Indicator(100, 100)), 20, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.BLUEBERRY, Item.MOSS), EscapeType.SURRENDER
    ),
    ZHABARIB(
        R.string.zhabarib, R.drawable.animal_zhabarib, IndicatorHealth(Indicator(150, 150)),
        IndicatorEndurance(Indicator(150, 150)), 10, Effect.NO_EFFECT,
        mutableListOf(), mutableListOf(Item.MUSHROOM_RAW, Item.FISH_RAW), EscapeType.SURRENDER
    )
}
