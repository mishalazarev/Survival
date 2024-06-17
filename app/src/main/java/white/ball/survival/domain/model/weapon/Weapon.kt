package white.ball.survival.domain.model.weapon

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.base_model.RecipeForItem
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot

enum class Weapon (
    override var nameItem: Int,
    override val descrItem: Int,
    override var imageId: Int,
    override var itemUse: ItemUse,
    val damage: Int,
    val itemEffect: Effect,
    override var recipe: List<ItemsSlot>,
) : RecipeForItem {

    WOODEN_SWORD(R.string.wooden_sword_recipe_item, R.string.wooden_sword_recipe_item_descr, R.drawable.recipe_item_wooden_sword,ItemUse.WEAPON, 10, Effect.NO_EFFECT, mutableListOf(
        ItemsSlot(Item.WOOD,3)
    )),
    STONE_SWORD(R.string.stone_sword_recipe_item, R.string.stone_sword_recipe_item_descr, R.drawable.recipe_item_stone_sword,ItemUse.WEAPON, 20, Effect.NO_EFFECT, mutableListOf(
        ItemsSlot(Item.STONE,2),
        ItemsSlot(Item.WOOD,1)
    )),
    MACE(R.string.mace_recipe_item, R.string.mace_recipe_item_descr, R.drawable.recipe_item_mace,ItemUse.WEAPON, 30, Effect.STUN, mutableListOf(
        ItemsSlot(Item.NURG_HORN,3),
        ItemsSlot(Item.WOOD,1),
        ItemsSlot(Item.STONE,1)
    )),
    KATANA(R.string.katana_recipe_item, R.string.katana_recipe_item_descr, R.drawable.recipe_item_katana, ItemUse.WEAPON, 30, Effect.POISONING, mutableListOf(
        ItemsSlot(Item.POISONED_POINT,2),
        ItemsSlot(Item.WOOD,1),
        ItemsSlot(Item.STONE,1)
    )),
    ARROW(R.string.arrow_recipe_item, R.string.arrow_recipe_item_descr, R.drawable.recipe_item_arrow, ItemUse.ARROW, 10, Effect.NO_EFFECT, mutableListOf(
        ItemsSlot(Item.LEAVES,1),
        ItemsSlot(Item.WOOD,1)
    )),
    POISONED_ARROW(R.string.poisoned_arrow_recipe_item, R.string.poisoned_arrow_recipe_item_descr, R.drawable.recipe_item_poisoned_arrow, ItemUse.ARROW, 20, Effect.POISONING, mutableListOf(
        ItemsSlot(Item.LEAVES,1),
        ItemsSlot(Item.WOOD,1),
        ItemsSlot(Item.POISONED_POINT,1)
    )),
    BOW(R.string.bow_recipe_item, R.string.bow_recipe_item_descr, R.drawable.recipe_item_bow, ItemUse.BOW, 20, Effect.NO_EFFECT, mutableListOf(
        ItemsSlot(Item.WOOD,2),
        ItemsSlot(Item.WEB,2)
    )),

}