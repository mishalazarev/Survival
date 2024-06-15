package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot

interface BuildRepository : Slot {
    override val nameItem: Int
    override val imageId: Int
    override val itemUse: ItemUse
    val placeForCook: Array<ItemsSlot?>?
    var cooking: Indicator?
    var placeForWater: ItemsSlot?
    var extractingWater: Indicator?
    val placeForAntiThief: Array<ItemsSlot?>
    var antiThief: Indicator?
    var placeForFruit: ItemsSlot?
    var plant: Plant?
    val recipe: List<ItemsSlot>
}