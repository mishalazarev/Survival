package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.SlotForItem
import white.ball.survival.domain.model.base_model.Item


interface ItemProperties {

    fun put(item: Item): Boolean

    fun take(item: Item)

    fun useItem(): Boolean

    fun joinItems(items: List<SlotForItem>): Boolean
}