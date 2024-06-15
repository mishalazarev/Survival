package white.ball.survival.domain.model.plant

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.repository.Plant

class OakTree(
    override val namePlant: Int = R.string.oak,
    override var currentLevel: Int = 0,
    override var indicatorGrow: Indicator = Indicator(0, 110),
    override var isPlantWatered: Boolean = false,
    override var commonProcess: Array<Int> = arrayOf(R.drawable.plant_planted_seed, R.drawable.plant_sprout, R.drawable.tree_young_oak, R.drawable.tree_adult_oak),
    override val destroyPlant: Array<Array<ItemsSlot>> = arrayOf(arrayOf(), arrayOf(),
        arrayOf(ItemsSlot(Item.WOOD, 1), ItemsSlot(Item.LEAVES, 1)),
        arrayOf(ItemsSlot(Item.WOOD, 3), ItemsSlot(Item.LEAVES, 3))),
    override var fruit: ItemsSlot? = null,
    override var ability: PlantAbility = PlantAbility.ADULT
) : Plant()