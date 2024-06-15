package white.ball.survival.domain.model.plant


import white.ball.survival.R
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.repository.Plant
import white.ball.survival.domain.model.base_model.Item
data class AloePlant(
    override val namePlant: Int = R.string.aloe_item,
    override var currentLevel: Int = 0,
    override var indicatorGrow: Indicator = Indicator(0, 110),
    override var isPlantWatered: Boolean = false,
    override var commonProcess: Array<Int> = arrayOf(R.drawable.plant_planted_seed, R.drawable.plant_aloe_sprout, R.drawable.plant_aloe_young, R.drawable.plant_aloe_adult),
    override val destroyPlant: Array<Array<ItemsSlot>> = arrayOf(arrayOf(), arrayOf(), arrayOf(
        ItemsSlot(Item.ALOE, 1)), arrayOf(ItemsSlot(Item.ALOE, 2))),
    override var fruit: ItemsSlot? = ItemsSlot(Item.ALOE, 3),
    override var ability: PlantAbility = PlantAbility.ADULT
) : Plant() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AloePlant

        if (namePlant != other.namePlant) return false
        if (currentLevel != other.currentLevel) return false
        if (indicatorGrow != other.indicatorGrow) return false
        if (isPlantWatered != other.isPlantWatered) return false
        if (!commonProcess.contentEquals(other.commonProcess)) return false
        if (fruit != other.fruit) return false
        return ability == other.ability
    }

    override fun hashCode(): Int {
        var result = namePlant
        result = 31 * result + currentLevel
        result = 31 * result + indicatorGrow.hashCode()
        result = 31 * result + isPlantWatered.hashCode()
        result = 31 * result + commonProcess.contentHashCode()
        result = 31 * result + fruit.hashCode()
        result = 31 * result + ability.hashCode()
        return result
    }


}