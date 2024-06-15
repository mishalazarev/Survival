package white.ball.survival.domain.repository

import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility

abstract class Plant {
    abstract val namePlant: Int
    abstract var currentLevel: Int
    abstract var indicatorGrow: Indicator
    abstract var isPlantWatered: Boolean
    abstract var commonProcess: Array<Int>
    abstract val destroyPlant: Array<Array<ItemsSlot>>
    abstract var fruit: ItemsSlot?
    abstract var ability: PlantAbility
}