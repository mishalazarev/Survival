package white.ball.survival.domain.service

import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.IndicatorEndurance
import white.ball.survival.domain.model.extension_model.IndicatorHealth
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.AnimalRepository
import kotlin.random.Random

typealias AnimalListener = (currentAnimal: Animal) -> Unit

class AnimalAction(
    private val animal: Animal
) : AnimalRepository {

    private lateinit var currentAnimal: Animal
    private val listenersAnimal = mutableListOf<AnimalListener>()

    private lateinit var indicatorHealth: IndicatorHealth

    private lateinit var indicatorEndurance: IndicatorEndurance

    override fun loadAnimal() {
        currentAnimal = animal
        with(animal) {
            indicatorHealth = health
            indicatorEndurance = endurance
        }
    }

    override fun getCurrentAnimal(): Animal = currentAnimal

    override fun dropExp(): Int {
        val maxExp = currentAnimal.damage + (currentAnimal.health.indicator.maxPercent / 10 * 2)
        return Random.nextInt(10,maxExp)
    }

    override fun hit(playerResisting: List<Effect>): Effect {
        val randomEffectValue = Random.nextInt(0, 3)
        val hitValue = Random.nextInt(currentAnimal.damage)
        val animalEffect = useAbility()
        if (currentAnimal.endurance.indicator.percent < hitValue) {
            Effect.NO_EFFECT.changeValueDamage(0)
            return Effect.NO_EFFECT
        } else if (animalEffect == Effect.NO_EFFECT || randomEffectValue == 0 || randomEffectValue == 1) {
            currentAnimal.endurance.indicator.percent -= hitValue
            Effect.NO_EFFECT.changeValueDamage(hitValue)
            return Effect.NO_EFFECT
        } else {
            var effectResistingFlag = false
            if(playerResisting.isEmpty()) {
                effectResistingFlag = true
                currentAnimal.endurance.indicator.percent -= hitValue
                animalEffect.changeValueDamage(hitValue)
                return animalEffect
            } else {
                playerResisting.forEach {
                    val letterEffectPlayer = it.flagLetterEffect
                    if (letterEffectPlayer[0] == currentAnimal.effect.flagLetterEffect[0] &&
                        letterEffectPlayer[1] == 'S' && currentAnimal.effect.flagLetterEffect[1] == 'A'
                    ) effectResistingFlag = true
                    if (effectResistingFlag) {
                        currentAnimal.endurance.indicator.percent -= hitValue
                        animalEffect.changeValueDamage(hitValue)
                        return animalEffect
                    }
                }
            }
        }
        return animalEffect
    }

    override fun scrollAttack(): Effect {
        val boxScrolls = mutableListOf(Effect.FIRE_BALL, Effect.REGENERATION, Effect.WEAKNESS,  Effect.POISONING, Effect.HEALING)
        val randomIndex = Random.nextInt(0, boxScrolls.size)
        val resultEffect = boxScrolls[randomIndex]

        when (resultEffect) {
            Effect.POISONING -> {
                resultEffect.damageValue = 30
            }
            Effect.WEAKNESS -> {
                resultEffect.damageValue = 30
            }
            Effect.FIRE_BALL -> {
                resultEffect.damageValue = 50
            }
            Effect.REGENERATION, Effect.HEALING -> {}
            else -> {}
        }

        return resultEffect
    }

    override fun scrollRegeneration(): Effect {
        startEffect(Effect.REGENERATION, 45_000)
        return Effect.REGENERATION
    }

    override fun getItemDrop(): List<ItemsSlot> {
        val countDropItem = Random.nextInt(1,3)
        val itemDropDirty = mutableListOf<ItemsSlot>()
        for (index  in 0..countDropItem) {
            itemDropDirty.add(ItemsSlot(animal.drop[Random.nextInt(0,animal.drop.size)],0))
        }
        val itemDropClean = itemDropDirty.distinct()
        for (index in itemDropClean.indices) {
            itemDropDirty.forEach {
                if (itemDropClean[index].item.nameItem == it.item.nameItem) itemDropClean[index].count += 1
            }
        }
        return itemDropClean
    }

    override fun startEffect(effect: Effect, time: Int) {
        when (effect) {
            Effect.POISONING -> {
                if (effect.timeAction != 0L) {
                    currentAnimal.health.isPoisoning = true
                    effect.timeAction -= 1000
                    currentAnimal.health.indicator.percent -= 1
                } else {
                    currentAnimal.health.isPoisoning = false
                    currentAnimal.effectHaveDeny.remove(effect)
                }
            }

            Effect.WEAKNESS -> {
                if (effect.timeAction != 0L) {
                    effect.timeAction -= 1000
                    currentAnimal.endurance.isWeakYet = true
                } else {
                    currentAnimal.endurance.isWeakYet = false
                    currentAnimal.effectHaveDeny.remove(effect)
                }
            }

            Effect.REGENERATION -> {
                if (effect.timeAction != 0L) {
                    effect.timeAction -= 1000
                    currentAnimal.health.isRegeneration = true
                    currentAnimal.health.isPoisoning = false
                    currentAnimal.endurance.isWeakYet = false
                    if (currentAnimal.health.indicator.percent != currentAnimal.health.indicator.maxPercent) currentAnimal.health.indicator.percent += 1
                } else {
                    currentAnimal.health.isRegeneration = false
                    currentAnimal.effectHaveDeny.remove(effect)
                }
            }

            else -> {}
        }
        notifyAnimalChanges()
    }

    override fun updateEnduranceParameter() {
        var denyEffect: Effect? = null

        currentAnimal.effectHaveDeny.forEach {
            if (it.nameText == Effect.WEAKNESS.nameText) denyEffect = it
        }

        val randomValueEndurance = if (denyEffect == null) {
            generateRandomValueForParameters()
        } else {
            0
        }

        if (currentAnimal.health.indicator.percent != 100) {
            val checkOnCountEndurance = currentAnimal.endurance.indicator.percent + randomValueEndurance
            currentAnimal.endurance.indicator.percent = checkEnduranceOnSumParameter(checkOnCountEndurance)
        }
    }

    private fun checkEnduranceOnSumParameter(value: Int): Int {
        if (value > currentAnimal.endurance.indicator.maxPercent) return currentAnimal.endurance.indicator.maxPercent
        return value
    }

    private fun generateRandomValueForParameters(): Int = Random.nextInt(1, 11)

    override fun useAbility(): Effect {
        if (currentAnimal.effect == Effect.NO_EFFECT) return Effect.NO_EFFECT
        return currentAnimal.effect
    }

    override fun addAnimalListener(animalListener: AnimalListener) {
        listenersAnimal.add(animalListener)
        animalListener.invoke(currentAnimal)

    }

    override fun upDateInformation() {
        notifyAnimalChanges()
    }

    private fun notifyAnimalChanges() {
        listenersAnimal.forEach { it.invoke(currentAnimal) }
    }
}