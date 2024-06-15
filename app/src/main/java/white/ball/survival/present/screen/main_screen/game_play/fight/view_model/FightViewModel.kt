package white.ball.survival.present.screen.main_screen.game_play.fight.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.repository.GameTimeRepository
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.Plant
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.*
import kotlin.math.ceil

class FightViewModel(
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository,
    private val gameTimeRepository: GameTimeRepository,
) : ViewModel() {

    private lateinit var animalAction: AnimalAction

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

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location
    private val locationListener: CurrentLocationListener = {
        _location.value = it
    }

    private val _gameTime = MutableLiveData<GameTime>()
    val gameTime: LiveData<GameTime> = _gameTime
    private val gameTimeValueListener: GameTimeListener = {
        _gameTime.value = it
    }

    var repeatAttackAnimal = 1

    var countHint = 0

    init {
        gameTimeRepository.loadGameTime()
        playerRepository.addPlayerListener(playerListener)
        interactionWithEnvironmentRepository.addListenerCurrentLocation(locationListener)
        gameTimeRepository.addListenerGameTime(gameTimeValueListener)
    }

    fun loadAnimal() {
        var plantMonster: Plant? = null
        if (interactionWithEnvironmentRepository.getCurrentLocation().build?.plant != null) {
            plantMonster = interactionWithEnvironmentRepository.getCurrentLocation().build?.plant
        }

        animalAction = if (gameTime.value!!.gameDaysLeft < 0) {
            AnimalAction(interactionWithEnvironmentRepository.callThFinalBoss())
        } else if (plantMonster != null && plantMonster.currentLevel == (plantMonster.commonProcess.size - 1) && plantMonster.ability == PlantAbility.MONSTER) {
            interactionWithEnvironmentRepository.getCurrentLocation().build!!.plant = null
            AnimalAction(Animal.FRUIT)
        } else {
            AnimalAction(interactionWithEnvironmentRepository.generateAnimalByLocation())
        }

        if (player.value!!.effectHaveDeny.isEmpty()) {
            Effect.values().forEach {
                it.timeAction = 90_000
            }
        }

        interactionWithEnvironmentRepository.restoreAnimalParameters()
        animalAction.loadAnimal()
        animalAction.addAnimalListener(animalListener)
    }

    fun getCurrentNameAnimal(): Int = animalAction.getCurrentAnimal().nameAnimal

    fun giftForLife(): ItemsSlot = playerRepository.giveItemForLife()

    fun showPutOnItems(): List<ItemsSlot?> = playerRepository.showMyPutOn()

    fun setGameTimeParameters(newGameDayLeft: Int, newGameTime: Long) {
        gameTimeRepository.setGameTime(newGameDayLeft, newGameTime)
        gameTimeRepository.upDateInformation()
    }

    fun hitByPlayer(): Effect {
        val resultEffect = playerRepository.hit()
        _animal.value!!.health.indicator.percent -= resultEffect.damageValue

        if (resultEffect != Effect.NO_EFFECT) {
            player.value!!.effectHaveDeny.add(resultEffect)
        }

        notifyChanges()
        return resultEffect
    }

    fun superPunchByPlayer(): Effect {
        val resultEffect = playerRepository.superPunch()
        animal.value!!.health.indicator.percent -= resultEffect.damageValue

        return setEffectByAnimal(resultEffect)
    }

    fun bowShotByPlayer(): Effect {
        val resultEffect = playerRepository.bowShot()
        if (resultEffect.damageValue != 0) {
            animal.value!!.health.indicator.percent -= resultEffect.damageValue
        }

        return setEffectByAnimal(resultEffect)
    }

    fun scrollAttackByPlayer(): Effect {
        val resultEffect = playerRepository.scrollAttack()
        animal.value!!.health.indicator.percent -= resultEffect.damageValue

        return setEffectByAnimal(resultEffect)
    }

    fun scrollRegenerationByPlayer() {
        player.value!!.effectHaveDeny.clear()
        player.value!!.effectHaveDeny.add(playerRepository.scrollRegeneration())
    }

    private fun setEffectByAnimal(resultEffect: Effect): Effect {
        with(animal.value!!) {
            for (index in 0 until effectHaveDeny.size) {
                if (effectHaveDeny[index].nameText == resultEffect.nameText) {
                    effectHaveDeny[index] = resultEffect
                    notifyChanges()
                    return resultEffect
                }
            }
        }

        if (resultEffect != Effect.NO_EFFECT && resultEffect != Effect.FIRE_BALL && !animal.value!!.effectHaveDeny.contains(resultEffect)
            && !animal.value!!.effectHaveDeny.contains(Effect.REGENERATION)) {
            animal.value!!.effectHaveDeny.add(resultEffect)
        }

        return resultEffect
    }


    fun hitByAnimal(): Effect {
        val resisting = mutableListOf<Effect>()
        player.value!!.placeForPutOn[2]?.also{
            resisting.addAll((it.item as Armor).armorEffect)
        }

        val animalEffect = animalAction.hit(resisting)

        setDamageValueByPlayer(animalEffect)

        resisting.forEach {
            if (it.flagLetterEffect == "WS" && animalEffect.flagLetterEffect == "WA") {
                val stopWeakness = Effect.STOP_WEAKNESS
                stopWeakness.damageValue = animalEffect.damageValue
                notifyChanges()
                return stopWeakness
            } else if (it.flagLetterEffect == "PS" && animalEffect.flagLetterEffect == "PA") {
                val stopPoisoning = Effect.STOP_POISONING
                stopPoisoning.damageValue = animalEffect.damageValue
                notifyChanges()
                return stopPoisoning
            }
        }

        if (player.value!!.effectHaveDeny.contains(animalEffect)) {
            for (index in player.value!!.effectHaveDeny.indices) {
                if (player.value!!.effectHaveDeny[index].nameText == animalEffect.nameText) {
                    player.value!!.effectHaveDeny[index] = animalEffect
                }
            }
        } else if (player.value!!.effectHaveDeny.contains(Effect.REGENERATION) && animalEffect != Effect.STUN) {
            val stopEffect = Effect.STOP_POISONING
            stopEffect.damageValue = animalEffect.damageValue
        } else if (animalEffect != Effect.NO_EFFECT && animalEffect != Effect.FIRE_BALL) {
            player.value!!.effectHaveDeny.add(animalEffect)
        }

        notifyChanges()
        return animalEffect
    }

    fun scrollAttackByAnimal(): Effect {
        val animalEffect = animalAction.scrollAttack()

        setDamageValueByPlayer(animalEffect)

        return setEffectByPlayer(animalEffect)
    }

    private fun setDamageValueByPlayer(resultEffect: Effect) {
        if (player.value!!.placeForPutOn[2] == null) {
            player.value!!.health.indicator.percent -= resultEffect.damageValue
        } else {
            val valueDamageWithArmor: Double = resultEffect.damageValue / (player.value!!.placeForPutOn[2]!!.item as Armor).defence
            val resultValue: Int = ceil(valueDamageWithArmor).toInt()
            resultEffect.damageValue = resultValue

            player.value!!.health.indicator.percent -= resultEffect.damageValue
        }
    }

    fun scrollRegenerationByAnimal(effect: Effect) {
        if (effect.nameText == Effect.REGENERATION.nameText) {
            animal.value!!.effectHaveDeny.clear()
            animal.value!!.effectHaveDeny.add(animalAction.scrollRegeneration())
        } else if (effect.nameText == Effect.HEALING.nameText) {
            animal.value!!.health.indicator.percent += 100
            while (animal.value!!.health.indicator.maxPercent < animal.value!!.health.indicator.percent) {
                animal.value!!.health.indicator.percent = animal.value!!.health.indicator.maxPercent
            }
        }
    }

    private fun setEffectByPlayer(animalEffect: Effect): Effect {
        val resisting = mutableListOf<Effect>()
        player.value!!.placeForPutOn[2]?.also{
            resisting.addAll((it.item as Armor).armorEffect)
        }

        with(player.value!!) {
            for (index in 0 until effectHaveDeny.size) {
                if (effectHaveDeny[index].nameText == animalEffect.nameText) {
                    effectHaveDeny[index] = animalEffect
                    notifyChanges()
                    return animalEffect
                }
            }
        }

        resisting.forEach {
            if (it.flagLetterEffect == "WS" && animalEffect.flagLetterEffect == "WA") {
                val stopWeakness = Effect.STOP_WEAKNESS
                stopWeakness.damageValue = animalEffect.damageValue
                notifyChanges()
                return stopWeakness
            } else if (it.flagLetterEffect == "PS" && animalEffect.flagLetterEffect == "PA") {
                val stopPoisoning = Effect.STOP_POISONING
                stopPoisoning.damageValue = animalEffect.damageValue
                notifyChanges()
                return stopPoisoning
            }
        }

        if (animalEffect != Effect.NO_EFFECT && animalEffect != Effect.FIRE_BALL && !animal.value!!.effectHaveDeny.contains(animalEffect)
            && animalEffect.nameText != Effect.REGENERATION.nameText && animalEffect.nameText != Effect.HEALING.nameText) {
            player.value!!.effectHaveDeny.add(animalEffect)
        }

        return animalEffect
    }

    fun surrender(animal: Animal): Boolean = playerRepository.surrender(animal)

    fun startEffectByPlayer(effect: Effect, time: Int) {
        playerRepository.startEffect(effect, time)
    }

    fun startEffectByAnimal(effect: Effect, time: Int) {
        animalAction.startEffect(effect, time)
    }

    fun upDatePhysiologicalParameters() {
        animalAction.updateEnduranceParameter()
        animalAction.upDateInformation()
        playerRepository.upDateInformation()
        playerRepository.isUpDatePhysiologicalParameters()
    }

    fun notifyChanges() {
        playerRepository.upDateInformation()
        animalAction.upDateInformation()
    }

    fun startCooking() {
        interactionWithEnvironmentRepository.startCooking(player.value!!.news)
    }

    fun startAntiThief() {
        interactionWithEnvironmentRepository.startAntiThief(player.value!!.news)
    }

    fun startExtractingWater() {
        interactionWithEnvironmentRepository.startExtractingWater(player.value!!.news)
    }

    fun startGrowingPlant() {
        return interactionWithEnvironmentRepository.startGrowingPlant(player.value!!.news)
    }
}