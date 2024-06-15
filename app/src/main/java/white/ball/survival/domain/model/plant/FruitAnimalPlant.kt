package white.ball.survival.domain.model.plant

import white.ball.survival.R
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.Plant

data class FruitAnimalPlant(
    override val namePlant: Int = R.string.fruit_animal,
    override var currentLevel: Int = 0,
    override var indicatorGrow: Indicator = Indicator(0, 5),
    override var isPlantWatered: Boolean = false,
    override var commonProcess: Array<Int> = arrayOf(R.drawable.plant_planted_seed, R.drawable.plant_sprout, R.drawable.plant_young_animal_fruit, R.drawable.animal_fruit),
    override val destroyPlant: Array<Array<ItemsSlot>> = arrayOf(arrayOf(), arrayOf(), arrayOf(), arrayOf()),
    override var fruit: ItemsSlot? = null,
    override var ability: PlantAbility = PlantAbility.MONSTER
) : Plant() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FruitAnimalPlant

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
        result = 31 * result + (fruit?.hashCode() ?: 0)
        result = 31 * result + ability.hashCode()
        return result
    }

}