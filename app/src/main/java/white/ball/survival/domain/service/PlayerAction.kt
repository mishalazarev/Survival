package white.ball.survival.domain.service

import white.ball.survival.R
import white.ball.survival.domain.convertor.UIRepository
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.IndicatorEndurance
import white.ball.survival.domain.model.extension_model.IndicatorHealth
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.Level
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.PlayerRepository
import kotlin.random.Random

typealias PlayerListener = (currentPlayer: Player) -> Unit

class PlayerAction(
    private val uiRepository: UIRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository,
    private var player: Player
) : PlayerRepository {

    private var currentPlayer: Player
    private val listenersPlayer = mutableListOf<PlayerListener>()

    private var indicatorHealth: IndicatorHealth
    private var indicatorEndurance: IndicatorEndurance
    private var indicatorSatiety: Indicator
    private var indicatorThirst: Indicator

    private val levelUp = Level()
    private val news = News()

    init {
        currentPlayer = player
        with(currentPlayer) {
            indicatorHealth = health
            indicatorEndurance = endurance
            indicatorSatiety = satiety.indicator
            indicatorThirst = thirst.indicator
        }
    }

    override fun loadPlayer(newPlayer: Player) {
        player = newPlayer
        currentPlayer = newPlayer
        with(currentPlayer) {
            indicatorHealth = health
            indicatorEndurance = endurance
            indicatorSatiety = satiety.indicator
            indicatorThirst = thirst.indicator
        }
        notifyPlayerChanges()
    }

    override fun getPlayer(): Player = currentPlayer

    override fun showMyBackpack(): List<ItemsSlot> = currentPlayer.backpack

    override fun showRecipes(): Array<Weapon> = Weapon.values()

    override fun showMyPutOn(): List<ItemsSlot?> = currentPlayer.placeForPutOn

    override fun setPutOn(index: Int, newItemsForPutOn: ItemsSlot?): Int {
        val currentPlaceForPutOn = currentPlayer.placeForPutOn
        val currentPlaceBackpack = currentPlayer.backpack

        if (newItemsForPutOn != null && currentPlaceForPutOn[index] != null) {
            if (currentPlaceForPutOn[index]!!.item is Weapon && newItemsForPutOn.item is Weapon) {
                with(currentPlayer) {
                    damage -= (currentPlaceForPutOn[index]!!.item as Weapon).damage
                    damage += newItemsForPutOn.item.damage
                }
            }

            currentPlaceBackpack.remove(newItemsForPutOn)
            currentPlaceBackpack.add(currentPlaceForPutOn[index]!!)
            currentPlayer.placeForPutOn[index] = newItemsForPutOn
            notifyPlayerChanges()
            return newItemsForPutOn.item.imageId
        } else if (currentPlaceForPutOn[index] != null && newItemsForPutOn == null) {
            if (currentPlaceForPutOn[index]!!.item is Weapon) currentPlayer.damage -= (currentPlaceForPutOn[index]!!.item as Weapon).damage

            currentPlaceBackpack.add(currentPlaceForPutOn[index]!!)
            currentPlaceForPutOn[index] = null
            notifyPlayerChanges()
            return uiRepository.getIconForItemResImage(index)
        } else if (currentPlaceForPutOn[index] == null && newItemsForPutOn != null) {
            if (newItemsForPutOn.item is Weapon) currentPlayer.damage += newItemsForPutOn.item.damage

            currentPlaceForPutOn[index] = newItemsForPutOn
            currentPlaceBackpack.remove(newItemsForPutOn)
            notifyPlayerChanges()
            return newItemsForPutOn.item.imageId
        }
        return uiRepository.getIconForItemResImage(index)
    }

    override fun divideIntoQualityGroups(itemForDivide: ItemsSlot) {
        val dividedItems = ItemsSlot(itemForDivide.item, itemForDivide.count / 2)
        if (itemForDivide.count % 2 == 0) {
            currentPlayer.backpack.add(dividedItems)
        } else {
            currentPlayer.backpack.add(ItemsSlot(dividedItems.item, dividedItems.count + 1))
        }
        currentPlayer.backpack.add(dividedItems)
        currentPlayer.backpack.remove(itemForDivide)
    }


    override fun createItem(recipeItem: Slot): Boolean {
        val itemsBackpackForCreate = mutableListOf<ItemsSlot>()
        val clearList = mutableListOf<ItemsSlot>()
        val flagListBoolean = mutableListOf<Boolean>()
        val currentBackpack = currentPlayer.backpack

        if (recipeItem is Weapon) {
            for (mainIndex in recipeItem.recipe.indices) {
                itemsBackpackForCreate.add(ItemsSlot(recipeItem.recipe[mainIndex].item, 0))
                Loop@ for (secondIndex in currentBackpack.indices) {
                    if (recipeItem.recipe[mainIndex].item.nameItem == currentBackpack[secondIndex].item.nameItem) {
                        itemsBackpackForCreate[mainIndex].count += currentBackpack[secondIndex].count
                        if (checkOnCountItemsForCreateRecipeItems(
                                recipeItem.recipe[mainIndex],
                                itemsBackpackForCreate[mainIndex],
                                currentBackpack[secondIndex]
                            )
                        ) break@Loop
                    }
                }
            }

            for (index in itemsBackpackForCreate.indices) {
                flagListBoolean.add(
                    index,
                    itemsBackpackForCreate[index].count == recipeItem.recipe[index].count
                )
            }

            flagListBoolean.forEach {
                if (!it) {
                    itemsBackpackForCreate.removeAll(currentBackpack)
                    itemsBackpackForCreate.forEach { itemsEmpty ->
                        if (itemsEmpty.count == 0) clearList.add(itemsEmpty)
                    }
                    itemsBackpackForCreate.removeAll(clearList)
                    currentBackpack.addAll(itemsBackpackForCreate)
                    return false
                }
            }

            if (recipeItem.itemUse == ItemUse.ARROW) {
                takeItems(mutableListOf(ItemsSlot(recipeItem, 4)))
            } else {
                currentBackpack.add(ItemsSlot(recipeItem, 1))
            }

            notifyPlayerChanges()
            return true
        } else if (recipeItem is Armor) {
            for (mainIndex in recipeItem.recipe.indices) {
                itemsBackpackForCreate.add(ItemsSlot(recipeItem.recipe[mainIndex].item, 0))
                Loop@ for (secondIndex in currentBackpack.indices) {
                    if (recipeItem.recipe[mainIndex].item.nameItem == currentBackpack[secondIndex].item.nameItem) {
                        itemsBackpackForCreate[mainIndex].count += currentBackpack[secondIndex].count
                        if (checkOnCountItemsForCreateRecipeItems(
                                recipeItem.recipe[mainIndex],
                                itemsBackpackForCreate[mainIndex],
                                currentBackpack[secondIndex]
                            )
                        ) break@Loop
                    }
                }
            }

            for (index in itemsBackpackForCreate.indices) {
                flagListBoolean.add(
                    index,
                    itemsBackpackForCreate[index].count == recipeItem.recipe[index].count
                )
            }

            flagListBoolean.forEach {
                if (!it) {
                    itemsBackpackForCreate.removeAll(currentBackpack)
                    itemsBackpackForCreate.forEach { itemsEmpty ->
                        if (itemsEmpty.count == 0) clearList.add(itemsEmpty)
                    }
                    itemsBackpackForCreate.removeAll(clearList)
                    currentBackpack.addAll(itemsBackpackForCreate)
                    return false
                }
            }

            currentBackpack.add(ItemsSlot(recipeItem, 1))
            notifyPlayerChanges()
            return true
        } else if (recipeItem is BuildRepository) {
            for (mainIndex in recipeItem.recipe.indices) {
                itemsBackpackForCreate.add(ItemsSlot(recipeItem.recipe[mainIndex].item, 0))
                Loop@ for (secondIndex in currentBackpack.indices) {
                    if (recipeItem.recipe[mainIndex].item.nameItem == currentBackpack[secondIndex].item.nameItem) {
                        itemsBackpackForCreate[mainIndex].count += currentBackpack[secondIndex].count
                        if (checkOnCountItemsForCreateRecipeItems(
                                recipeItem.recipe[mainIndex],
                                itemsBackpackForCreate[mainIndex],
                                currentBackpack[secondIndex]
                            )
                        ) break@Loop
                    }
                }
            }

            for (index in itemsBackpackForCreate.indices) {
                flagListBoolean.add(
                    index,
                    itemsBackpackForCreate[index].count == recipeItem.recipe[index].count
                )
            }

            flagListBoolean.forEach {
                if (!it) {
                    itemsBackpackForCreate.removeAll(currentBackpack)
                    itemsBackpackForCreate.forEach { itemsEmpty ->
                        if (itemsEmpty.count == 0) clearList.add(itemsEmpty)
                    }
                    itemsBackpackForCreate.removeAll(clearList)
                    currentBackpack.addAll(itemsBackpackForCreate)
                    return false
                }
            }

            currentBackpack.add(ItemsSlot(recipeItem, 1))
            notifyPlayerChanges()
            return true
        }

        notifyPlayerChanges()
        return false
    }

    override fun joinSimilarItemsSlot(itemsSlot: ItemsSlot) {
        val commonSimilarItemsSlot = ItemsSlot(itemsSlot.item, count = 0)
        val currentBackpackPlayer = currentPlayer.backpack
        var index = 0

        while (index < currentBackpackPlayer.size) {
            if (currentBackpackPlayer[index].item.nameItem == commonSimilarItemsSlot.item.nameItem) {
                commonSimilarItemsSlot.count += currentBackpackPlayer[index].count
                currentPlayer.backpack.remove(currentBackpackPlayer[index])
                if (index != 0) index--
            } else {
                index++
            }
        }

        while (commonSimilarItemsSlot.count >= 10) {
            commonSimilarItemsSlot.count -= 10
            currentBackpackPlayer.add(ItemsSlot(commonSimilarItemsSlot.item,10))
        }

        if (commonSimilarItemsSlot.count != 0) currentBackpackPlayer.add(ItemsSlot(commonSimilarItemsSlot.item, commonSimilarItemsSlot.count))
    }

    override fun giveItemForLife(): ItemsSlot {
        val currentBackpack = currentPlayer.backpack

        val result: ItemsSlot = currentBackpack[(Math.random() * currentBackpack.size).toInt()]
        throwItemInCurrentBackpackAway(result)
        notifyPlayerChanges()
        return result
    }

    override fun takeItems(newItemsDrop: MutableList<ItemsSlot>) {
        val currentBackpack = currentPlayer.backpack

        if (currentBackpack.isNotEmpty()) {
            for (mainIndex in currentBackpack.indices) {
                var index = 0
                while (index < newItemsDrop.size) {
                    val maxSlotSize =
                        if (currentBackpack[mainIndex].item.itemUse != ItemUse.ARROW) {
                            10
                        } else {
                            20
                        }
                    if (currentBackpack[mainIndex].item.nameItem == newItemsDrop[index].item.nameItem && currentBackpack[mainIndex].count < maxSlotSize) {
                        interactionWithEnvironmentRepository.equalizeCountOfItems(currentBackpack[mainIndex], newItemsDrop[index], currentBackpack)
                        newItemsDrop.remove(newItemsDrop[index])
                        index--
                    }
                    index++
                }
            }
        }

        if (newItemsDrop.isNotEmpty()) currentBackpack.addAll(newItemsDrop)

        notifyPlayerChanges()
    }

    override fun takeExp(expValue: Int) {
        currentPlayer.exp += expValue
        val checkOnNewLevel = levelUp.checkOnNewLevel(currentPlayer.exp)
        if (currentPlayer.level != checkOnNewLevel && checkOnNewLevel != 1) {
            with(currentPlayer) {
                level = checkOnNewLevel
                studyPoints += 10

                health.indicator.percent = health.indicator.maxPercent
                endurance.indicator.percent = endurance.indicator.maxPercent

                satiety.indicator.percent = 100
                thirst.indicator.percent = 100
            }
            currentPlayer.news.add(0, news.getLevelUpNews(currentPlayer.level))
            notifyPlayerChanges()
        }
    }

    override fun upParameter(keyParameter: Char) {
        when (keyParameter) {
            'h' -> indicatorHealth.indicator.maxPercent += 10
            'e' -> indicatorEndurance.indicator.maxPercent += 10
            'd' -> currentPlayer.damage += 5
            else -> 0
        }
        currentPlayer.studyPoints -= 5
        upDateInformation()
    }

    override fun throwItemInCurrentBackpackAway(items: ItemsSlot) {
        if (items.count == 1) currentPlayer.backpack.remove(items)
        items.count -= 1
        notifyPlayerChanges()
    }

    override fun throwItemInCurrentItemsPutOnAway(item: ItemsSlot) {
        currentPlayer.placeForPutOn.remove(item)
        notifyPlayerChanges()
    }

    override fun openShell(itemShell: ItemsSlot) {
        val randomSeedBox = arrayOf(Item.SEED_ALOE, Item.SEED_BONE, Item.SEED_FRUIT_ANIMAL, Item.SEED_OAK, Item.SEED_WHEAT)
        val randomSeed = randomSeedBox[Random.nextInt(0 , randomSeedBox.size)]

        removeOneItemAndAddOneItem(itemShell, randomSeed)
        notifyPlayerChanges()
    }

    override fun openScroll(scrollOpening: ItemsSlot) {
        val randomScrollBox = arrayListOf(Item.SCROLL_POISON, Item.SCROLL_WEAK, Item.SCROLL_FIRE_BALL, Item.SCROLL_REGENERATION)
        val randomScroll = randomScrollBox[Random.nextInt(0, randomScrollBox.size)]

        removeOneItemAndAddOneItem(scrollOpening, randomScroll)
        notifyPlayerChanges()
    }

    override fun throwAllItemsAway(items: ItemsSlot) {
        currentPlayer.backpack.remove(items)
        notifyPlayerChanges()
    }

    override fun death() {
        currentPlayer.backpack.clear()
        with(currentPlayer) {
            health.indicator.maxPercent = 100
            health.indicator.percent = 100
            health.isPoisoning = false

            endurance.indicator.maxPercent = 100
            endurance.indicator.percent = 125
            endurance.isWeakYet = false

            damage = 20

            satiety.indicator.percent = 100
            thirst.indicator.percent = 100

            placeForPutOn = mutableListOf(null, null, null, null)
            effectHaveDeny.clear()
            news.clear()

            exp = 0
        }
        upDateInformation()
    }

    override fun thiefStealItem() {
        if (interactionWithEnvironmentRepository.getCurrentLocation().build!!.placeForAntiThief[0] != null) return
        if (currentPlayer.backpack.isEmpty()) return

        val randomItem = currentPlayer.backpack[Random.nextInt(0, currentPlayer.backpack.size)]
        val indexInRandomItemInBackpack = currentPlayer.backpack.indexOf(randomItem)

        if (randomItem.count != 1) {
            currentPlayer.backpack[indexInRandomItemInBackpack].count -= 1
        } else {
            currentPlayer.backpack.removeAt(indexInRandomItemInBackpack)
        }
        currentPlayer.news.add(0, news.getItemStoleNews(uiRepository.getString(randomItem.item.nameItem)))
        notifyPlayerChanges()
    }

    override fun sleep(): Boolean {
        indicatorHealth.isPoisoning = false
        if (indicatorSatiety.percent != 0) return false
        val afterSleptHealthPercent: Int =
            if (indicatorHealth.indicator.percent >= 50 && indicatorHealth.indicator.percent > 20) {
                (indicatorHealth.indicator.percent * 1.5).toInt()
            } else {
                indicatorHealth.indicator.percent - 20
            }

        val afterSleptSatietyPercent =
            if (indicatorSatiety.percent >= 50 && indicatorSatiety.percent > 17) {
                (indicatorSatiety.percent / 1.5).toInt()
            } else {
                indicatorSatiety.percent - 17
            }

        indicatorSatiety.percent = afterSleptSatietyPercent
        if (afterSleptHealthPercent <= 100) {
            indicatorHealth.indicator.percent = afterSleptHealthPercent
        } else {
            indicatorHealth.indicator.percent = 100
        }
        return true
    }


    override fun takeFood(foods: ItemsSlot): Boolean {
        val currentBackpack = currentPlayer.backpack

        if (foods.item.itemUse == ItemUse.FOOD || foods.item.itemUse == ItemUse.FOR_COOK || foods.item.itemUse == ItemUse.DRINK) {
            foods.item as Item

            val checkOnCountHealth = currentPlayer.health.indicator.percent + foods.item.nutritionalValue
            val checkOnCountEndurance = currentPlayer.endurance.indicator.percent + foods.item.moistureValue
            val checkOnCountThirst = currentPlayer.thirst.indicator.percent + (foods.item.moistureValue )
            val checkOnCountSatiety = currentPlayer.satiety.indicator.percent + (foods.item.nutritionalValue)

            currentPlayer.health.indicator.percent = checkHealthOnSumParameter(checkOnCountHealth)
            currentPlayer.endurance.indicator.percent = checkEnduranceOnSumParameter(checkOnCountEndurance)
            currentPlayer.thirst.indicator.percent = checkOnSumParameter(checkOnCountThirst)
            currentPlayer.satiety.indicator.percent = checkOnSumParameter(checkOnCountSatiety)

            foods.count -= 1
            if (foods.count == 0) {
                var index = 0
                while (index < currentBackpack.size) {
                    if (currentBackpack[index].item.nameItem == foods.item.nameItem && currentBackpack[index].count == 0) currentBackpack.remove(
                        currentBackpack[index--]
                    )
                    index++
                }
            }

            when (foods.item) {
                Item.FRUIT -> {
                    getRandomEffectByFood(foods.item.ability, 1)
                }
                Item.MUSHROOM_RAW -> {
                    getRandomEffectByFood(foods.item.ability, 2)
                }
                else -> {
                    foods.item.ability.forEach { foodEffect ->
                        if (currentPlayer.effectHaveDeny.contains(foodEffect)) {
                            currentPlayer.effectHaveDeny.forEach { effectInPlayer ->
                                if (effectInPlayer.nameText == foodEffect.nameText) effectInPlayer.timeAction = foodEffect.timeAction
                            }
                        } else if (!currentPlayer.effectHaveDeny.contains(Effect.REGENERATION)) {
                            currentPlayer.effectHaveDeny.add(foodEffect)
                            startEffect(foodEffect, foodEffect.timeAction.toInt())
                        }
                    }
                }
            }

            notifyPlayerChanges()
            return true
        } else {
            return false
        }
    }

    override fun startEffect(effect: Effect, time: Int) {
        when (effect) {
            Effect.POISONING -> {
                if (effect.timeAction != 0L) {
                    currentPlayer.health.isPoisoning = true
                    effect.timeAction -= 1000
                    currentPlayer.health.indicator.percent -= 1
                } else {
                    currentPlayer.health.isPoisoning = false
                    currentPlayer.effectHaveDeny.remove(effect)
                    effect.timeAction = 90_000L
                }
            }

            Effect.STOP_POISONING -> {
                if (currentPlayer.effectHaveDeny.contains(Effect.POISONING)) currentPlayer.effectHaveDeny.remove(Effect.POISONING)
                currentPlayer.effectHaveDeny.remove(Effect.STOP_POISONING)
                currentPlayer.health.isPoisoning = false
            }

            Effect.WEAKNESS -> {
                if (effect.timeAction != 0L) {
                    effect.timeAction -= 1000
                    currentPlayer.endurance.isWeakYet = true
                } else {
                    currentPlayer.endurance.isWeakYet = false
                    currentPlayer.effectHaveDeny.remove(effect)
                    effect.timeAction = 90_000L
                }
            }

            Effect.STOP_WEAKNESS -> {
                if (currentPlayer.effectHaveDeny.contains(Effect.WEAKNESS)) currentPlayer.effectHaveDeny.remove(Effect.WEAKNESS)
                currentPlayer.effectHaveDeny.remove(Effect.STOP_WEAKNESS)
                currentPlayer.endurance.isWeakYet = false
            }

            Effect.REGENERATION -> {
                if (effect.timeAction != 0L) {
                    effect.timeAction -= 1000
                    with(currentPlayer) {
                        health.isRegeneration = true
                        health.isPoisoning = false
                        endurance.isRegeneration = true
                        endurance.isWeakYet = false
                    }
                    if (currentPlayer.health.indicator.percent != currentPlayer.health.indicator.maxPercent) currentPlayer.health.indicator.percent += 1
                    if (currentPlayer.endurance.indicator.percent != currentPlayer.endurance.indicator.maxPercent) currentPlayer.endurance.indicator.percent += 1
                    if (currentPlayer.effectHaveDeny.contains(Effect.POISONING)) currentPlayer.effectHaveDeny.remove(Effect.POISONING)
                    if (currentPlayer.effectHaveDeny.contains(Effect.WEAKNESS)) currentPlayer.effectHaveDeny.remove(Effect.WEAKNESS)
                } else {
                    with(currentPlayer) {
                        health.isRegeneration = false
                        endurance.isRegeneration = false
                    }
                    currentPlayer.effectHaveDeny.remove(effect)
                    effect.timeAction = 90_000L
                }
            }

            else -> {}
        }
        notifyPlayerChanges()
    }

    override fun isUpDatePhysiologicalParameters(): Boolean {
        var denyEffect: Effect? = null

        currentPlayer.effectHaveDeny.forEach {
            if (it.nameText == Effect.WEAKNESS.nameText) denyEffect = it
        }

        val randomValueHealth = generateRandomHealthValueForMainParameters()
        val randomValueEndurance = if (denyEffect == null) {
            generateRandomEnduranceValueForMainParameters()
        } else {
            0
        }

        if (currentPlayer.satietyAndThirstSettingFlag) {
            val randomValueSatiety = generateRandomValueForSecondParameters()
            var randomValueThirst = generateRandomValueForSecondParameters()

            if (currentPlayer.satiety.indicator.percent >= 70) {
                val checkOnCountHealth = currentPlayer.health.indicator.percent + randomValueHealth
                var checkOnCountThirst = currentPlayer.thirst.indicator.percent - randomValueThirst
                val checkOnCountEndurance = currentPlayer.endurance.indicator.percent + randomValueEndurance

                currentPlayer.satiety.indicator.percent -= randomValueSatiety

                currentPlayer.endurance.indicator.percent = checkEnduranceOnSumParameter(checkOnCountEndurance)
                currentPlayer.thirst.indicator.percent = checkOnMinusParameter(checkOnCountThirst)
                randomValueThirst = generateRandomValueForSecondParameters()
                checkOnCountThirst = currentPlayer.thirst.indicator.percent + randomValueThirst
                currentPlayer.thirst.indicator.percent = checkOnSumParameter(checkOnCountThirst)
                currentPlayer.health.indicator.percent =
                    checkHealthOnSumParameter(checkOnCountHealth)
                return true
            } else if (currentPlayer.satiety.indicator.percent == 0) {
                if (currentPlayer.health.indicator.percent == 0 && currentPlayer.thirst.indicator.percent == 0) return false
                val checkOnCountHealth = currentPlayer.health.indicator.percent - randomValueHealth

                currentPlayer.health.indicator.percent = checkOnMinusParameter(checkOnCountHealth)
                if (checkOnCountHealth == 0) return false
            } else {
                val checkOnCountSatiety = currentPlayer.satiety.indicator.percent - randomValueSatiety
                var checkOnCountThirst = currentPlayer.thirst.indicator.percent - randomValueThirst
                val checkOnCountEndurance = currentPlayer.endurance.indicator.percent + randomValueEndurance

                if (checkOnCountSatiety == 0) {
                    currentPlayer.satiety.indicator.percent = 0

                    checkOnCountThirst = currentPlayer.thirst.indicator.percent - randomValueThirst
                    currentPlayer.thirst.indicator.percent = checkOnMinusParameter(checkOnCountThirst)
                } else {
                    currentPlayer.satiety.indicator.percent = checkOnMinusParameter(checkOnCountSatiety)

                    currentPlayer.endurance.indicator.percent = checkEnduranceOnSumParameter(checkOnCountEndurance)

                    currentPlayer.thirst.indicator.percent = checkOnMinusParameter(checkOnCountThirst)
                    randomValueThirst = generateRandomValueForSecondParameters()
                    checkOnCountThirst = currentPlayer.thirst.indicator.percent + randomValueThirst
                    currentPlayer.thirst.indicator.percent = checkOnSumParameter(checkOnCountThirst)
                }

                return true
            }
            return false
        } else {
            val checkOnCountHealth = currentPlayer.health.indicator.percent + randomValueHealth
            val checkOnCountEndurance = currentPlayer.endurance.indicator.percent + randomValueEndurance
            currentPlayer.endurance.indicator.percent = checkEnduranceOnSumParameter(checkOnCountEndurance)
            currentPlayer.health.indicator.percent = checkHealthOnSumParameter(checkOnCountHealth)
            return true
        }
    }

    override fun moveAnotherLocation(): Boolean {
        interactionWithEnvironmentRepository.changeLocation(currentPlayer)
        return true
    }

    override fun useAbility(): Effect {
        return (currentPlayer.placeForPutOn[2]!!.item as Weapon).itemEffect
    }

    override fun hit(): Effect {
        val damageValue = Random.nextInt(5, currentPlayer.damage)
        if (indicatorEndurance.indicator.percent < damageValue) {
            val resultEffect = Effect.NO_EFFECT
            resultEffect.damageValue = 0
            return resultEffect
        }
        indicatorEndurance.indicator.percent -= damageValue
        val resultEffect = Effect.NO_EFFECT
        resultEffect.damageValue = damageValue
        return resultEffect
    }

    override fun superPunch(): Effect {
        val randomEffect = Random.nextInt(0, 2)

        if (randomEffect == 0) {
            return hit()
        } else {
            val damageValue = Random.nextInt(5, currentPlayer.damage)
            if (damageValue > indicatorEndurance.indicator.percent) return Effect.NO_EFFECT
            val resultEffect = (currentPlayer.placeForPutOn[0]!!.item as Weapon).itemEffect
            resultEffect.damageValue = damageValue

            indicatorEndurance.indicator.percent -= damageValue
            return resultEffect
        }
    }

    override fun bowShot(): Effect {
        val targetArrow = currentPlayer.placeForPutOn[1]!!
        val randomDamageValue = Random.nextInt(20, currentPlayer.damage)
        var effectResult = Effect.NO_EFFECT
        if (randomDamageValue > currentPlayer.endurance.indicator.percent) {
            effectResult.damageValue = 0
            return effectResult
        }
        currentPlayer.endurance.indicator.percent -= randomDamageValue

        val indexEffectRandom = if ((targetArrow.item as Weapon).itemEffect == Effect.POISONING){
            Random.nextInt(0,2)
        } else {
            0
        }

        if (targetArrow.count == 1) {
            currentPlayer.placeForPutOn[1] = null
        } else {
            currentPlayer.placeForPutOn[1]!!.count -= 1
        }

        when (indexEffectRandom) {
            0 -> {
                effectResult.damageValue = randomDamageValue
            }
            1 -> {
                effectResult = Effect.POISONING
                effectResult.damageValue = randomDamageValue
            }
        }
        return effectResult
    }

    override fun scrollAttack(): Effect {
        val resultEffect = (currentPlayer.placeForPutOn[3]!!.item as Item).ability[0]

        if (currentPlayer.placeForPutOn[3]!!.count == 1) {
            currentPlayer.placeForPutOn[3] = null
        } else {
            currentPlayer.placeForPutOn[3]!!.count -= 1
        }

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
            else -> throw IllegalArgumentException("Attack effect is unknown")
        }

        return resultEffect
    }

    override fun scrollRegeneration(): Effect {
        startEffect(Effect.REGENERATION, 45_000)
        currentPlayer.satiety.indicator.percent = 100
        currentPlayer.thirst.indicator.percent = 100

        if (currentPlayer.placeForPutOn[3]!!.count == 1) {
            currentPlayer.placeForPutOn[3] = null
        } else {
            currentPlayer.placeForPutOn[3]!!.count -= 1
        }

        return Effect.REGENERATION
    }

    override fun setBuild(buildItem: ItemsSlot?) {
        interactionWithEnvironmentRepository.setBuildThisLocation(buildItem, currentPlayer.backpack)
    }

    override fun surrender(animal: Animal): Boolean {
        var checkOnIsNotEmptyPutOn = false

        val currentPutOn = currentPlayer.placeForPutOn
        val currentBackpack = currentPlayer.backpack

        if (currentPutOn[0] != null || currentPutOn[1] != null && currentPutOn[2] != null && currentPutOn[3] != null) {
            checkOnIsNotEmptyPutOn = true
        } else if (currentBackpack.isNotEmpty()) {
            checkOnIsNotEmptyPutOn = true
        }

        return checkOnIsNotEmptyPutOn
    }

    override fun upDateInformation() {
        notifyPlayerChanges()
    }

    override fun addPlayerListener(playerListener: PlayerListener) {
        listenersPlayer.add(playerListener)
        playerListener.invoke(currentPlayer)
    }

    override fun showNewsList(): List<News> = currentPlayer.news

    override fun clearNewsList() {
        currentPlayer.news.clear()
    }

    private fun notifyPlayerChanges() {
        listenersPlayer.forEach { it.invoke(currentPlayer) }
    }
    
    private fun removeOneItemAndAddOneItem(itemWillRemove: ItemsSlot, itemWillAdd: Item) {
        var isHaveSimilarItemsWhichNotMaxSize = true

        if (itemWillRemove.count == 1) {
            currentPlayer.backpack.remove(itemWillRemove)
        } else {
            itemWillRemove.count -= 1
        }

        currentPlayer.backpack.forEach {
            if (it.item.nameItem == itemWillAdd.nameItem && it.count != 10) {
                it.count += 1
                isHaveSimilarItemsWhichNotMaxSize = false
                return
            }
        }

        if (isHaveSimilarItemsWhichNotMaxSize) {
            currentPlayer.backpack.add(ItemsSlot(itemWillAdd, 1))
        }
    }

    private fun getRandomEffectByFood(effectFoodList: List<Effect>, maxSizeForAdd: Int,) {
        for (index in 0 until maxSizeForAdd) {
            val randomIndex = Random.nextInt(0, effectFoodList.size)
            val randomEffectByFood = effectFoodList[randomIndex]
            if (currentPlayer.effectHaveDeny.contains(randomEffectByFood)) {
                for (index in currentPlayer.effectHaveDeny.indices) {
                    if (currentPlayer.effectHaveDeny[index].nameText == randomEffectByFood.nameText) currentPlayer.effectHaveDeny[index] = randomEffectByFood
                }
            } else {
                currentPlayer.effectHaveDeny.add(randomEffectByFood)
            }
            startEffect(randomEffectByFood, randomEffectByFood.timeAction.toInt())
        }
    }

    private fun checkOnCountItemsForCreateRecipeItems(
        needQuantityItems: ItemsSlot,
        itemsForCreate: ItemsSlot,
        backpackSlot: ItemsSlot
    ): Boolean {
        return if (itemsForCreate.count < needQuantityItems.count) {
            false
        } else if (itemsForCreate.count > needQuantityItems.count) {
            backpackSlot.count = itemsForCreate.count - needQuantityItems.count
            itemsForCreate.count = needQuantityItems.count
            true
        } else {
            currentPlayer.backpack.remove(backpackSlot)
            true
        }
    }

    private fun checkEnduranceOnSumParameter(value: Int): Int {
        if (value > currentPlayer.endurance.indicator.maxPercent) return currentPlayer.endurance.indicator.maxPercent
        return value
    }

    private fun checkHealthOnSumParameter(value: Int): Int {
        if (value > currentPlayer.health.indicator.maxPercent) return currentPlayer.health.indicator.maxPercent
        return value
    }

    private fun checkOnSumParameter(value: Int): Int {
        if (value > 100) return 100
        return value
    }

    private fun checkOnMinusParameter(value: Int): Int {
        if (value < 0) return 0
        return value
    }

    private fun generateRandomValueForSecondParameters(): Int = Random.nextInt(1, 3)

    private fun generateRandomHealthValueForMainParameters(): Int = Random.nextInt(5, currentPlayer.health.indicator.maxPercent / 10)

    private fun generateRandomEnduranceValueForMainParameters(): Int = Random.nextInt(5, currentPlayer.endurance.indicator.maxPercent / 10)

    companion object {

        @JvmStatic
        val ICON_FOR_ITEM_ARROW = R.drawable.icon_arrow_for_item
        @JvmStatic
        val ICON_FOR_ITEM_ARMOR = R.drawable.icon_armor_for_item
        @JvmStatic
        val ICON_FOR_ITEM_WEAPON = R.drawable.icon_weapon_for_item
        @JvmStatic
        val ICON_FOR_ITEM_SCROLL = R.drawable.icon_scroll_for_item
        @JvmStatic
        val ICON_FOR_ITEM_MEAT = R.drawable.icon_meat_for_item
        @JvmStatic
        val ICON_FOR_ITEM_WOOD = R.drawable.icon_wood_for_item
        @JvmStatic
        val ICON_FOR_ITEM_GEM = R.drawable.icon_gem_for_item
    }
}