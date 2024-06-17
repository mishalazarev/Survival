package white.ball.survival.domain.model.armor

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.base_model.RecipeForItem
import white.ball.survival.domain.model.base_model.SlotForItem
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot

enum class Armor (
    override var nameItem: Int,
    override val descrItem: Int,
    override var imageId: Int,
    override var itemUse: ItemUse,
    val defence: Double,
    val armorEffect: List<Effect>,
    override val recipe: List<ItemsSlot>,
    val playerInArmor: Int,
) : SlotForItem, RecipeForItem {

    PUCHICK_ARMOR(R.string.puchick_armor_recipe_item, R.string.puchick_armor_recipe_item_descr, R.drawable.recipe_item_puchick_armor, ItemUse.ARMOR, 1.5, mutableListOf(), mutableListOf(
        ItemsSlot(Item.PUCHICK_PLATE, 9), ItemsSlot(Item.LEAVES, 3), ItemsSlot(Item.WOOD, 1)
    ), R.drawable.animal_player_in_puchick_armor),
    NURG_ARMOR(R.string.nurg_armor_recipe_item, R.string.nurg_armor_recipe_item_descr, R.drawable.recipe_item_nurg_armor, ItemUse.ARMOR, 2.0, mutableListOf(Effect.STOP_WEAKNESS), mutableListOf(
        ItemsSlot(Item.NURG_PLATE, 12), ItemsSlot(Item.STONE, 3), ItemsSlot(Item.LEAVES, 3)), R.drawable.animal_player_in_nurg_armor),
    SPORK_ARMOR(R.string.spork_armor_recipe_item, R.string.spork_armor_recipe_item_descr, R.drawable.recipe_item_spork_armor, ItemUse.ARMOR, 2.0, mutableListOf(Effect.STOP_WEAKNESS, Effect.STOP_POISONING),
        mutableListOf(ItemsSlot(Item.SPORK_PLATE, 12), ItemsSlot(Item.STONE, 5), ItemsSlot(Item.LEAVES, 5)), R.drawable.animal_player_in_spork_armor)
}