package white.ball.survival.domain.service

import white.ball.survival.R
import white.ball.survival.domain.convertor.UIRepository
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Hut
import white.ball.survival.domain.model.build.StoneHouse
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.model.plant.AloePlant
import white.ball.survival.domain.model.plant.FruitAnimalPlant
import white.ball.survival.domain.model.plant.MeatTree
import white.ball.survival.domain.model.plant.OakTree
import white.ball.survival.domain.model.plant.WheatPlant
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import kotlin.math.ceil
import kotlin.random.Random

typealias LocationListener = (locationList: List<Location>) -> Unit
typealias CurrentLocationListener = (currentLocation: Location) -> Unit

class InteractionWithEnvironment(
    private val uiRepository: UIRepository,
    private val locationList: List<Location>,
) : InteractionWithEnvironmentRepository {

    private lateinit var commonLocationList: List<Location>
    private val listenersCommonLocationList = mutableListOf<LocationListener>()

    private lateinit var currentLocation: Location
    private val listenersCurrentLocation = mutableListOf<CurrentLocationListener>()

    private var currentAnimals: List<Animal> = mutableListOf()
    private val news = News()

    init {
        loadMap(locationList)
    }

    override fun changeLocation(player: Player): Boolean {
        return if ((player.endurance.indicator.percent >= 25 || player.health.indicator.percent == 0) && !currentLocation.isPlayerFindHere) {
            loadMap(locationList)
            player.endurance.indicator.percent -= 25
            notifyCurrentLocationChanges()
            true
        } else {
            false
        }
    }
    override fun loadMap(newLocationList: List<Location>) {
        newLocationList.forEach {
            if (it.isPlayerFindHere) {
                upDateData(it)
            }
        }
        commonLocationList = newLocationList

    }

    override fun setForCook(index: Int, newItemsForCook: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int {
        val currentPlaceForCook = currentLocation.build!!.placeForCook
        val correctIndex = when (index) {
            4 -> {
                0
            }
            5 -> {
                1
            }
            else -> {
                2
            }
        }

        if (newItemsForCook != null && currentPlaceForCook!![correctIndex] != null) {
            if (currentPlaceForCook[correctIndex]!!.item.nameItem == newItemsForCook.item.nameItem && currentPlaceForCook[correctIndex]!!.count == 10)
                return currentPlaceForCook[correctIndex]!!.item.imageId

            return when (newItemsForCook.item.itemUse) {
                ItemUse.FOR_COOK -> {
                    equalizeCountOfItems(currentPlaceForCook[0]!!, newItemsForCook, backpackByPlayer)
                    startCooking(newsList)
                    newItemsForCook.item.imageId
                }

                ItemUse.FOR_FIRE -> {
                    equalizeCountOfItems(currentPlaceForCook[1]!!, newItemsForCook, backpackByPlayer)
                    startCooking(newsList)
                    newItemsForCook.item.imageId
                }

                else -> throw IllegalArgumentException("This item is not for cook")
            }

        } else if (currentPlaceForCook!![correctIndex] != null && newItemsForCook == null) {
            backpackByPlayer.add(currentPlaceForCook[correctIndex]!!)
            currentPlaceForCook[correctIndex] = null
            return uiRepository.getIconForItemResImage(index)
        } else if (currentPlaceForCook[correctIndex] == null && newItemsForCook != null) {
            currentPlaceForCook[correctIndex] = newItemsForCook
            backpackByPlayer.remove(newItemsForCook)
            startCooking(newsList)
            return newItemsForCook.item.imageId
        }
        return uiRepository.getIconForItemResImage(index)
    }

    override fun setAntiThief(index: Int, itemPhosphorus: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int {
        val currentPlaceForAntiThief = currentLocation.build?.placeForAntiThief
        val correctIndex = if (index == 7) {
            0
        } else {
            1
        }

        if (itemPhosphorus != null && currentPlaceForAntiThief?.get(correctIndex) != null) {
            if (currentPlaceForAntiThief[correctIndex]!!.count == 10) return currentPlaceForAntiThief[0]!!.item.imageId

            currentPlaceForAntiThief[correctIndex]!!.count += itemPhosphorus.count
            if (currentPlaceForAntiThief[correctIndex]!!.count > 10) {
                itemPhosphorus.count = currentPlaceForAntiThief[correctIndex]!!.count - 10
                currentPlaceForAntiThief[correctIndex]!!.count -= itemPhosphorus.count
                notifyLocationsChanges()
                startAntiThief(newsList)
                return currentPlaceForAntiThief[correctIndex]!!.item.imageId
            } else if (currentPlaceForAntiThief[correctIndex]!!.count <= 10) {
                backpackByPlayer.remove(itemPhosphorus)
                notifyLocationsChanges()
                startAntiThief(newsList)
                return currentPlaceForAntiThief[correctIndex]!!.item.imageId
            }
        } else if (itemPhosphorus == null && currentPlaceForAntiThief?.get(correctIndex) != null) {
            backpackByPlayer.add(currentPlaceForAntiThief[correctIndex]!!)
            currentLocation.build!!.placeForAntiThief[correctIndex] = null
            notifyLocationsChanges()
            return uiRepository.getIconForItemResImage(index)
        } else if (itemPhosphorus != null && currentPlaceForAntiThief?.get(correctIndex) == null) {
            currentLocation.build!!.placeForAntiThief[correctIndex] = itemPhosphorus
            backpackByPlayer.remove(itemPhosphorus)
            notifyLocationsChanges()
            return itemPhosphorus.item.imageId
        }
        return uiRepository.getIconForItemResImage(index)
    }

    override fun setExtractWater(index: Int, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int {
       val currentPlaceForExtractWater = currentLocation.build!!.placeForWater

        return if (currentPlaceForExtractWater != null) {
            backpackByPlayer.add(currentPlaceForExtractWater)
            currentLocation.build!!.placeForWater = null
            uiRepository.getIconForItemResImage(9)
        }  else {
            uiRepository.getIconForItemResImage(9)
        }
    }

    override fun setPlantFruit(index: Int, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int {
        val currentPlaceForDrinkOfWater = currentLocation.build!!.placeForFruit


        if (currentPlaceForDrinkOfWater != null) {
            backpackByPlayer.add(currentPlaceForDrinkOfWater)
            currentLocation.build!!.placeForFruit = null
            notifyCurrentLocationChanges()
            return uiRepository.getIconForItemResImage(index)
        }
        return uiRepository.getIconForItemResImage(index)
    }

    override fun growPlant(seedItem: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>) {
        val plantMap = hashMapOf(
            R.string.seed_aloe to AloePlant(),
            R.string.seed_wheat to WheatPlant(),
            R.string.seed_fruit_animal to FruitAnimalPlant(),
            R.string.seed_bone to MeatTree(),
            R.string.see_oak to OakTree())

        currentLocation.build!!.plant = plantMap[seedItem.item.nameItem]
        startGrowingPlant(newsList)

        if (seedItem.count == 1) {
            backpackByPlayer.remove(seedItem)
        } else {
            seedItem.count -= 1
        }
        newsList.add(0, News().plantWantsToDrink(uiRepository.getString(currentLocation.build!!.plant!!.namePlant)))
    }

    override fun giveWaterForPlant(itemWater: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>) {
        if (itemWater.item.nameItem == Item.FLASK_WITH_WATER.nameItem) {
            if (itemWater.count == 1) {
                backpackByPlayer.remove(itemWater)
            } else {
                itemWater.count -= 1
            }
        } else if (itemWater.item.nameItem == Item.LARGE_FLASK_WITH_WATER.nameItem) {
            if (itemWater.count == 1) {
                backpackByPlayer.remove(itemWater)
            } else {
                itemWater.count -= 1
            }
            backpackByPlayer.add(ItemsSlot(Item.FLASK_WITH_WATER, 1))
        }
        currentLocation.build!!.plant!!.isPlantWatered = true

    }

    override fun destroyPlant(backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>) {
        val plant = currentLocation.build!!.plant!!
        backpackByPlayer.addAll(plant.destroyPlant[plant.currentLevel])
        val nameItemsText:MutableList<String> = mutableListOf()
        plant.destroyPlant[plant.currentLevel].forEach {
            nameItemsText.add(uiRepository.getString(it.item.nameItem))
        }
        newsList.add(0, News().getResource(plant.destroyPlant[plant.currentLevel], nameItemsText.toTypedArray()))
        currentLocation.build!!.plant = null

    }

    override fun startCooking(newsList: MutableList<News>) {
        val placeForCook = currentLocation.build?.placeForCook
        val indicatorCooking = currentLocation.build?.cooking

        if (placeForCook?.get(0) != null && placeForCook[1] != null) {
            if (placeForCook[2]?.count != 10) {

                if (indicatorCooking!!.percent == indicatorCooking.maxPercent) {
                    if (placeForCook[2] == null) {
                        placeForCook[2] = when (placeForCook[0]!!.item.nameItem) {
                            Item.MEAT_RAW.nameItem -> {
                                ItemsSlot(Item.MEAT_FRIED, 1)
                            }

                            Item.FISH_RAW.nameItem -> {
                                ItemsSlot(Item.FISH_FRIED, 1)
                            }

                            Item.MUSHROOM_RAW.nameItem -> {
                                ItemsSlot(Item.MUSHROOM_FRIED, 1)
                            }

                            Item.WHEAT.nameItem -> {
                                ItemsSlot(Item.BREAD, 1)
                            }

                            else -> throw IllegalArgumentException("unknown is item food")
                        }
                    } else {
                        placeForCook[2]!!.count += 1
                    }

                    if (placeForCook[0]!!.count == 1) {
                        placeForCook[0] = null
                    } else {
                        placeForCook[0]!!.count -= 1
                    }

                    if (placeForCook[1]!!.count == 1) {
                        placeForCook[1] = null
                    } else {
                        placeForCook[1]!!.count -= 1
                    }

                    indicatorCooking.percent = 0
                    newsList.add(
                        0,
                        news.getFoodCookedNews(uiRepository.getString(placeForCook[2]!!.item.nameItem))
                    )
                    notifyLocationsChanges()
                } else {
                    indicatorCooking.percent += 1

                }
            }
        }
    }

    override fun startAntiThief(newsList: MutableList<News>) {
        val placeForAntiThief = currentLocation.build?.placeForAntiThief
        val indicatorAntiThief = currentLocation.build?.antiThief

        if (placeForAntiThief?.get(0) != null) {
            if (placeForAntiThief[1] != null && placeForAntiThief[1]!!.count == 10) return

            if (indicatorAntiThief!!.percent == indicatorAntiThief.maxPercent) {
                if (placeForAntiThief[0]!!.count == 1) {
                    placeForAntiThief[0] = null
                } else {
                    placeForAntiThief[0]!!.count -= 1
                }

                if (placeForAntiThief[1] == null) {
                    placeForAntiThief[1] = ItemsSlot(Item.PHOSPHORUS_DIM, 1)
                } else {
                    placeForAntiThief[1]!!.count += 1
                }
                indicatorAntiThief.percent = 0
                newsList.add(0, news.getPhosphorusDimNews())
                notifyLocationsChanges()
            } else {
                indicatorAntiThief.percent += 1
            }
        }
    }

    override fun startExtractingWater(newsList: MutableList<News>) {
        val indicatorWaterExtracting = currentLocation.build?.extractingWater
        val placeForWater = currentLocation.build?.placeForWater

        if (currentLocation.build != null && (currentLocation.build is StoneHouse || currentLocation.build is WoodHouse || currentLocation.build is Hut ||
                    currentLocation.build is Well)) {
            if (placeForWater?.count == 10) return
            if (indicatorWaterExtracting!!.percent == indicatorWaterExtracting.maxPercent) {
                if (placeForWater == null) {
                    currentLocation.build!!.placeForWater = ItemsSlot(Item.FLASK_WITH_WATER, 1)
                } else {
                    placeForWater.count += 1
                }

                if (placeForWater?.count == 10) {
                    newsList.add(0, news.wellIsFull())
                } else {
                    newsList.add(0, news.waterExtract())
                }
                indicatorWaterExtracting.percent = 0
                notifyLocationsChanges()
            } else {
                indicatorWaterExtracting.percent += 1
            }
        }
    }

    override fun startGrowingPlant(newsList: MutableList<News>) {
        val plant = currentLocation.build?.plant
        val placeFruitOfPlant = currentLocation.build?.placeForFruit
        val news = News()

        if (currentLocation.build != null && plant != null && plant.isPlantWatered) {
            if (placeFruitOfPlant?.count == 10)  return

            if (plant.indicatorGrow.percent == plant.indicatorGrow.maxPercent) {
                    if (plant.currentLevel != plant.commonProcess.size - 1) {
                        plant.currentLevel += 1
                        if (plant.currentLevel == plant.commonProcess.size - 1 && plant.ability == PlantAbility.MONSTER) {
                            newsList.add(0, news.plantCameToLive(uiRepository.getString(plant.namePlant)))
                        } else {
                            newsList.add(0, news.plantWantsToDrink(uiRepository.getString(plant.namePlant)))
                        }
                    } else if (plant.currentLevel == plant.commonProcess.size - 1 && plant.ability == PlantAbility.
                        GIVE_FRUIT) {
                        if (placeFruitOfPlant == null) {
                            currentLocation.build?.placeForFruit = plant.fruit
                        } else if (placeFruitOfPlant.item.nameItem == plant.namePlant) {
                            placeFruitOfPlant.count += plant.fruit!!.count
                        }
                        newsList.add(0, news.plantGiveFruits(uiRepository.getString(plant.namePlant)))
                    } else if (plant.currentLevel == plant.commonProcess.size - 1 && plant.ability == PlantAbility.ADULT) {
                        newsList.add(0, news.plantBecomeAdult(plant.namePlant))
                    }

                plant.indicatorGrow.percent = 0
                plant.isPlantWatered = false
                notifyLocationsChanges()
                } else {
                    plant.indicatorGrow.percent += 1
                }
        }
    }

    override fun equalizeCountOfItems(currentItemsSlot: ItemsSlot, newItemsForPlus: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>) {
        currentItemsSlot.count += newItemsForPlus.count
        if (currentItemsSlot.count > 10) {
            var targetCount = 0
            newItemsForPlus.count = ceil((currentItemsSlot.count % 10).toDouble()).toInt()
            targetCount = currentItemsSlot.count - 10
            currentItemsSlot.count -= newItemsForPlus.count
            backpackByPlayer.add(ItemsSlot(newItemsForPlus.item, targetCount))
        } else {
            backpackByPlayer.remove(newItemsForPlus)
        }
    }

    override fun onClickedLocationPressed(location: Location) {
        commonLocationList.forEach {
            it.isPlayerFindHere = false
            if (it.nameLocationId == location.nameLocationId) it.isPlayerFindHere = true
        }
    }

    override fun getCurrentLocation(): Location = currentLocation

    override fun getCommonLocation(): List<Location> = commonLocationList

    override fun setCommonLocation(newLocationList: List<Location>) {
        commonLocationList = newLocationList
    }

    override fun setBuildThisLocation(build: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>)  {
        if (build == null) {
            currentLocation.build = null
            return
        }

        when(build.item) {
            is Bonfire -> {
                currentLocation.build = build.item
            }
            is Well -> {
                currentLocation.build = build.item
            }
            is Hut -> {
                currentLocation.build = build.item
            }
            is WoodHouse -> {
                currentLocation.build = build.item
            }
            is StoneHouse -> {
                currentLocation.build = build.item
            }
            else -> {
                throw IllegalArgumentException("Unknown is build")
            }
        }

        if (build.count == 1) {
            backpackByPlayer.remove(build)
        } else {
            build.count -= 1
        }
    }
    override fun callThFinalBoss(): Animal = Animal.ENSLAVER

    override fun generateAnimalByLocation(): Animal {
        val maxValue = currentLocation.animals.size

        return currentLocation.animals[Random.nextInt(maxValue)]
    }

    override fun restoreAnimalParameters() {
        currentAnimals.forEach{
            it.health.indicator.percent = it.health.indicator.maxPercent
            it.health.isPoisoning = false
            it.health.isRegeneration = false

            it.endurance.indicator.percent = it.endurance.indicator.maxPercent
            it.endurance.isWeakYet = false

            it.effectHaveDeny.clear()
        }
    }

    private fun upDateData(newLocation: Location) = with(newLocation) {
        currentLocation = newLocation
        currentAnimals = animals
    }

    override fun addListenerCurrentLocation(listenerCurrentLocation: CurrentLocationListener) {
        listenersCurrentLocation.add(listenerCurrentLocation)
        listenerCurrentLocation.invoke(currentLocation)
    }

    override fun addListenerLocation(commonLocationListener: LocationListener) {
        listenersCommonLocationList.add(commonLocationListener)
        commonLocationListener.invoke(locationList)
    }

    private fun notifyLocationsChanges() {
        listenersCommonLocationList.forEach { it.invoke(locationList) }
    }

    private fun notifyCurrentLocationChanges() {
        listenersCurrentLocation.forEach { it.invoke(currentLocation) }
    }
}