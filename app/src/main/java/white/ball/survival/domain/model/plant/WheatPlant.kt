package white.ball.survival.domain.model.plant

import white.ball.survival.R
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.repository.Plant

class WheatPlant(
    override val namePlant: Int = R.string.wheat,
    override var currentLevel: Int = 0,
    override var indicatorGrow: Indicator = Indicator(0,110),
    override var isPlantWatered: Boolean = false,
    override var commonProcess: Array<Int> = arrayOf(R.drawable.plant_planted_seed, R.drawable.plant_sprout, R.drawable.plant_young_wheat, R.drawable.plant_adult_wheat),
    override val destroyPlant: Array<Array<ItemsSlot>> = arrayOf(arrayOf(), arrayOf(), arrayOf(
        ItemsSlot(Item.WHEAT, 1)), arrayOf(ItemsSlot(Item.WHEAT, 2))),
    override var fruit: ItemsSlot? = ItemsSlot(Item.WHEAT, 3),
    override var ability: PlantAbility = PlantAbility.ADULT
) : Plant()