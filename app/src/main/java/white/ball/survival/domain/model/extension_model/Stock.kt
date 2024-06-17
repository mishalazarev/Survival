package white.ball.survival.domain.model.extension_model

import white.ball.survival.domain.model.base_model.SlotForItem

data class Stock(
    var sizeStorage: Int,
    var storageThings: List<SlotForItem>?,
)