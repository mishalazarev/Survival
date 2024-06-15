package white.ball.survival.domain.repository

import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.base_model.LiveObject
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.service.AnimalListener

interface AnimalRepository : LiveObject {

    fun hit(playerResisting: List<Effect>): Effect

    fun scrollAttack(): Effect

    fun scrollRegeneration(): Effect

    fun getItemDrop(): List<ItemsSlot>

    fun addAnimalListener(animalListener: AnimalListener)

    fun getCurrentAnimal(): Animal

    fun loadAnimal()

    fun dropExp(): Int

    fun startEffect(effect: Effect, time: Int)

    fun updateEnduranceParameter()
}