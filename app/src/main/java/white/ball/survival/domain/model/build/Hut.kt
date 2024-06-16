package white.ball.survival.domain.model.build

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.Plant

data class Hut(
    override val nameItem: Int = R.string.hut_recipe_item,
    override val descrItem: Int = R.string.hut_recipe_item_descr,
    override val imageId: Int = R.drawable.recipe_item_hut,
    override val itemUse: ItemUse = ItemUse.BUILD,
    override val placeForCook: Array<ItemsSlot?>? = arrayOfNulls(3),
    override var cooking: Indicator? = Indicator(0, 20),
    override var placeForWater: ItemsSlot? = null,
    override var extractingWater: Indicator? = Indicator(0, 100),
    override val placeForAntiThief: Array<ItemsSlot?> = arrayOfNulls(2),
    override var antiThief: Indicator? = Indicator(0, 1140),
    override var placeForFruit: ItemsSlot? = null,
    override var plant: Plant? = null,
    override val recipe: List<ItemsSlot>  = mutableListOf(ItemsSlot(Item.LEAVES, 6), ItemsSlot(Item.WOOD, 15)),
) : BuildRepository {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hut

        if (nameItem != other.nameItem) return false
        if (imageId != other.imageId) return false
        if (itemUse != other.itemUse) return false
        if (placeForCook != null) {
            if (other.placeForCook == null) return false
            if (!placeForCook.contentEquals(other.placeForCook)) return false
        } else if (other.placeForCook != null) return false
        if (cooking != other.cooking) return false
        if (placeForWater != other.placeForWater) return false
        if (extractingWater != other.extractingWater) return false
        if (!placeForAntiThief.contentEquals(other.placeForAntiThief)) return false
        if (antiThief != other.antiThief) return false
        if (placeForFruit != other.placeForFruit) return false
        if (plant != other.plant) return false
        return recipe == other.recipe
    }

    override fun hashCode(): Int {
        var result = nameItem
        result = 31 * result + imageId
        result = 31 * result + itemUse.hashCode()
        result = 31 * result + (placeForCook?.contentHashCode() ?: 0)
        result = 31 * result + (cooking?.hashCode() ?: 0)
        result = 31 * result + (placeForWater?.hashCode() ?: 0)
        result = 31 * result + (extractingWater?.hashCode() ?: 0)
        result = 31 * result + placeForAntiThief.contentHashCode()
        result = 31 * result + (antiThief?.hashCode() ?: 0)
        result = 31 * result + (placeForFruit?.hashCode() ?: 0)
        result = 31 * result + (plant?.hashCode() ?: 0)
        result = 31 * result + recipe.hashCode()
        return result
    }

}
