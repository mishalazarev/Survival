package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.base_model.Item


interface ItemProperties {

    fun put(item: Item): Boolean

    fun take(item: Item)

    fun useItem(): Boolean

    fun joinItems(items: List<Slot>): Boolean
}