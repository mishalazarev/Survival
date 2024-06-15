package white.ball.survival.domain.model.base_model

import white.ball.survival.R
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemUse

enum class Item (
    override var nameItem: Int,
    override var imageId: Int,
    override var itemUse: ItemUse,
    val nutritionalValue: Int,
    val moistureValue: Int,
    val ability: List<Effect>
) : Slot {

    SEED_IN_SHELL(R.string.seed_in_shell, R.drawable.item_seed_in_shell, ItemUse.SEED_IN_SHELL, 0, 0, mutableListOf()),
    SEED_ALOE(R.string.seed_aloe, R.drawable.item_aloe_seed, ItemUse.OPENED_SEED, 0, 0, mutableListOf()),
    SEED_WHEAT(R.string.seed_wheat, R.drawable.item_wheat_seed, ItemUse.OPENED_SEED, 10, 0, mutableListOf()),
    SEED_FRUIT_ANIMAL(R.string.seed_fruit_animal, R.drawable.item_fruit_animal_seed, ItemUse.OPENED_SEED, 0, 0, mutableListOf()),
    SEED_BONE(R.string.seed_bone, R.drawable.item_bone_seed, ItemUse.OPENED_SEED, 0, 0, mutableListOf()),
    SEED_OAK(R.string.see_oak, R.drawable.item_oak_seed, ItemUse.OPENED_SEED, 0, 0, mutableListOf()),

    LARGE_FLASK_WITH_WATER(R.string.item_large_flask_with_water, R.drawable.item_large_flask_with_water, ItemUse.DRINK,0,100, mutableListOf()),
    FLASK_WITH_WATER(R.string.item_flask_with_water, R.drawable.item_flask_with_water, ItemUse.DRINK, 0, 50, mutableListOf()),
    FISH_FRIED(R.string.fish_fried_item, R.drawable.item_fish_fried, ItemUse.FOOD,55,10, mutableListOf()),
    FISH_RAW(R.string.fish_raw_item, R.drawable.item_fish_raw, ItemUse.FOR_COOK,30,10, mutableListOf()),
    MEAT_FRIED(R.string.meat_fried_item, R.drawable.item_meat_fried, ItemUse.FOOD,55,10, mutableListOf()),
    MEAT_RAW(R.string.meat_raw_item, R.drawable.item_meat_raw, ItemUse.FOR_COOK,30,10, mutableListOf()),
    WHEAT(R.string.wheat, R.drawable.item_wheat, ItemUse.FOR_COOK, 0, 0, mutableListOf()),
    BREAD(R.string.bread, R.drawable.item_bread, ItemUse.FOOD, 70, 0, mutableListOf()),


    WOOD(R.string.wood_item, R.drawable.item_wood, ItemUse.FOR_FIRE,0,0, mutableListOf()),
    LEAVES(R.string.leaves_item, R.drawable.item_leaves, ItemUse.FOR_FIRE,0,0, mutableListOf()),
    STONE(R.string.stone_item, R.drawable.item_stone, ItemUse.INFO,0,0, mutableListOf()),
    WEB(R.string.web_item, R.drawable.item_web, ItemUse.INFO,0,0, mutableListOf()),
    NURG_HORN(R.string.nurg_born_item, R.drawable.item_nurg_horn, ItemUse.INFO,0,0, mutableListOf()),
    NURG_PLATE(R.string.nurg_plate_item, R.drawable.item_nurg_plate, ItemUse.INFO,0,0, mutableListOf()),
    PHOSPHORUS_BRIGHT(R.string.phosphorus_bright, R.drawable.item_phosphorus_bright, ItemUse.ANTI_THIEF, 0, 0, mutableListOf()),
    PHOSPHORUS_DIM(R.string.phosphorus_dim, R.drawable.item_phosphorus_dim, ItemUse.FOR_FIRE, 0, 0, mutableListOf()),
    POISONED_POINT(R.string.poisoned_point_item, R.drawable.item_poisoned_point, ItemUse.INFO,0,0, mutableListOf()),
    PUCHICK_PLATE(R.string.puchik_plate_item, R.drawable.item_puchick_plate, ItemUse.INFO,0,0,mutableListOf()),
    SPORK_PLATE(R.string.spork_plate_item, R.drawable.item_spork_plate, ItemUse.INFO,0,0, mutableListOf()),

    FRUIT(R.string.fruit_item, R.drawable.item_fruit, ItemUse.FOOD,20,10, mutableListOf(Effect.STOP_POISONING,Effect.POISONING)),
    HONEY(R.string.honey_item, R.drawable.item_honey, ItemUse.FOOD,15,0, mutableListOf(Effect.REGENERATION)),
    MOSS(R.string.moss_item, R.drawable.item_moss, ItemUse.FOOD,10,0, mutableListOf(Effect.STOP_WEAKNESS)),
    MUSHROOM_RAW(R.string.mushroom_raw_item, R.drawable.item_mushroom_raw, ItemUse.FOR_COOK,0,0, mutableListOf(Effect.POISONING,Effect.STOP_POISONING,Effect.WEAKNESS,Effect.STOP_WEAKNESS)),
    MUSHROOM_FRIED(R.string.mushroom_fried_item, R.drawable.item_mushroom_fried, ItemUse.FOOD, 50, 5, mutableListOf()),
    ALOE(R.string.aloe_item, R.drawable.item_aloe, ItemUse.FOOD,30,10, mutableListOf(Effect.REGENERATION)),
    BERRY(R.string.berry_item, R.drawable.item_berry, ItemUse.FOOD,25,10, mutableListOf(Effect.STOP_POISONING)),
    BLUEBERRY(R.string.blueberry_item, R.drawable.item_blueberry, ItemUse.FOOD,25,20, mutableListOf(Effect.REGENERATION)),
    CACTUS(R.string.cactus_item, R.drawable.item_cactus, ItemUse.FOOD,20,10, mutableListOf(Effect.REGENERATION)),

    SCROLL(R.string.scroll_item, R.drawable.item_scroll, ItemUse.SCROLL,0,0, mutableListOf()),
    SCROLL_FIRE_BALL(R.string.scroll_fire_ball, R.drawable.item_scroll_fire_ball, ItemUse.OPENED_SCROLL, 0, 0, mutableListOf(Effect.FIRE_BALL)),
    SCROLL_POISON(R.string.scroll_poison, R.drawable.item_scroll_poison, ItemUse.OPENED_SCROLL, 0, 0, mutableListOf(Effect.POISONING)),
    SCROLL_WEAK(R.string.scroll_weak, R.drawable.item_scroll_weak, ItemUse.OPENED_SCROLL, 0, 0, mutableListOf(Effect.WEAKNESS)),
    SCROLL_REGENERATION(R.string.scroll_regeneration, R.drawable.item_scroll_regeneration, ItemUse.OPENED_SCROLL, 0, 0, mutableListOf(Effect.REGENERATION))
}