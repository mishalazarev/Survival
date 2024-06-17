package white.ball.survival.domain.model.base_model

import white.ball.survival.R
import white.ball.survival.domain.model.News.NewsNotification
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.extension_model.*
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.model.base_model.Item

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

    var newsNotifications: MutableList<NewsNotification> = mutableListOf(),
    var backpack: MutableList<ItemsSlot> = mutableListOf(ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2),ItemsSlot(Item.WOOD, 2)
            , ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2)
        , ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2), ItemsSlot(Item.STONE, 2)
        , ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2)
        , ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2)
        , ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2), ItemsSlot(Item.LEAVES, 2)),
    var placeForPutOn: MutableList<ItemsSlot?> = mutableListOf(null, null, null, null),

    // effects
    var effectHaveDeny: MutableList<Effect> = mutableListOf(),

    // experience
    var level: Int = 1,
    var exp: Int = 0,
    var studyPoints: Int = 0
)
