package white.ball.survival.domain.model.extension_model

import white.ball.survival.domain.model.base_model.Slot

data class Stock(
    var sizeStorage: Int,
    var storageThings: List<Slot>?,
)