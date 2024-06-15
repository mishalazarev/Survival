package white.ball.survival.domain.model.extension_model

import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.repository.BuildRepository

data class ItemsSlot(
    val item: Slot,
    var count: Int
)
