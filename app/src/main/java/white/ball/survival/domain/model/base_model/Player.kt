package white.ball.survival.domain.model.base_model

import white.ball.survival.R
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.extension_model.*
import white.ball.survival.domain.model.weapon.Weapon

data class Player(
    var namePlayer: String = "",
    var satietyAndThirstSettingFlag: Boolean = true,
    var isHintsInGamePlayIncluded: Boolean = true,
    var isHintsInFightIncluded: Boolean = true,
    var imagePlayer: Int = R.drawable.animal_player,

    // main characteristic
    var health: IndicatorHealth = IndicatorHealth(Indicator(100,100),false),
    var endurance: IndicatorEndurance = IndicatorEndurance(Indicator(100,100),false),
    var damage: Int = 20,

    // physiological needs
    var satiety: IndicatorSatiety = IndicatorSatiety(Indicator(100,100)),
    var thirst: IndicatorThirst = IndicatorThirst(Indicator(100,100)),

    var news: MutableList<News> = mutableListOf(),
    var backpack: MutableList<ItemsSlot> = mutableListOf(ItemsSlot(Item.FLASK_WITH_WATER, 10) ,ItemsSlot(WoodHouse(), 1), ItemsSlot(Well(), 1), ItemsSlot(Item.PHOSPHORUS_DIM, 10),
        ItemsSlot(Bonfire(), 1),ItemsSlot(Weapon.KATANA,1), ItemsSlot(Item.SEED_IN_SHELL,10), ItemsSlot(Item.SCROLL, 1), ItemsSlot(Weapon.ARROW, 10),
        ItemsSlot(Item.LARGE_FLASK_WITH_WATER, 10), ItemsSlot(Item.MEAT_RAW, 10), ItemsSlot(Item.PHOSPHORUS_BRIGHT, 10),ItemsSlot(Item.WOOD, 10)),
    var placeForPutOn: MutableList<ItemsSlot?> = mutableListOf(null, null, null, null),

    // effects
    var effectHaveDeny: MutableList<Effect> = mutableListOf(),

    // experience
    var level: Int = 1,
    var exp: Int = 0,
    var studyPoints: Int = 0
)
