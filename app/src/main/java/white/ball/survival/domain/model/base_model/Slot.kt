package white.ball.survival.domain.model.base_model

import white.ball.survival.domain.model.extension_model.ItemUse

interface Slot {
    val nameItem: Int
    val imageId: Int
    val itemUse: ItemUse
}