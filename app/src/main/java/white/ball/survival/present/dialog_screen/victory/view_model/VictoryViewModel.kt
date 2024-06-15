package white.ball.survival.present.dialog_screen.victory.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.AnimalAction
import white.ball.survival.domain.service.AnimalListener
import white.ball.survival.domain.service.PlayerListener

class VictoryViewModel(
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository
) : ViewModel() {

    private lateinit var animalAction: AnimalAction
    private var gameTime: Long = 0L
    private var dayValue: Int = 0

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player
    private val playerListener: PlayerListener = {
        _player.value = it
    }

    private val _animal = MutableLiveData<Animal>()
    val animal: LiveData<Animal> = _animal
    private val animalListener: AnimalListener = {
        _animal.value = it
    }

    fun getCurrentAnimal(nameAnimal: Int) {
        Animal.values().forEach {
            if (it.nameAnimal == nameAnimal) _animal.value = it
        }
    }

    fun loadAnimal() {
        animalAction = AnimalAction(_animal.value!!)
        animalAction.loadAnimal()
        playerRepository.addPlayerListener(playerListener)
        animalAction.addAnimalListener(animalListener)
    }

    fun restoreAnimalParameters() {
        animalAction = AnimalAction(interactionWithEnvironmentRepository.generateAnimalByLocation())
//        interactionWithEnvironmentRepository.restoreAnimalParameters()
        animalAction.loadAnimal()
        animalAction.addAnimalListener(animalListener)
        animalAction.upDateInformation()
    }

    fun takeDropByPlayer(dropItems: MutableList<ItemsSlot>, expValue: Int) {
        playerRepository.takeItems(dropItems)
        playerRepository.takeExp(expValue)
        playerRepository.isUpDatePhysiologicalParameters()
    }

    fun getExp(): Int {
        return animalAction.dropExp()
    }

    fun getDropItems(): List<ItemsSlot> = animalAction.getItemDrop()

    fun saveGameTime(newGameTime: Long) {
        gameTime = newGameTime
    }

    fun getGameTime(): Long = gameTime

    fun setDay(newDayValue: Int) {
        dayValue = newDayValue
    }

    fun getDayValue(): Int = dayValue

}