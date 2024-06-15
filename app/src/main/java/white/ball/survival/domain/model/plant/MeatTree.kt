package white.ball.survival.domain.model.plant

import white.ball.survival.R
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.Plant
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.PlantAbility

class MeatTree(
    override val namePlant: Int = R.string.meat_tree,
    override var currentLevel: Int = 0,
    override var indicatorGrow: Indicator = Indicator(0, 110),
    override var isPlantWatered: Boolean = false,
    override var commonProcess: Array<Int> = arrayOf(R.drawable.plant_planted_seed, R.drawable.plant_sprout, R.drawable.tree_young_meat, R.drawable.tree_adult_meat),
    override val destroyPlant: Array<Array<ItemsSlot>> = arrayOf(arrayOf(), arrayOf(),
        arrayOf(ItemsSlot(Item.LEAVES, 1), ItemsSlot(Item.WOOD, 1), ItemsSlot(Item.MEAT_RAW, 1)),
        arrayOf(ItemsSlot(Item.LEAVES, 3), ItemsSlot(Item.WOOD, 3), ItemsSlot(Item.MEAT_RAW, 3))),
    override var fruit: ItemsSlot? = ItemsSlot(Item.MEAT_RAW,2),
    override var ability: PlantAbility = PlantAbility.GIVE_FRUIT,
) : Plant()