package white.ball.survival.domain.repository

import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.service.CurrentLocationListener
import white.ball.survival.domain.service.LocationListener

interface InteractionWithEnvironmentRepository {

    fun loadMap(newLocationList: List<Location>)
    fun setBuildThisLocation(build: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>)

    fun getCurrentLocation(): Location

    fun getCommonLocation(): List<Location>

    fun onClickedLocationPressed(location: Location)

    fun setCommonLocation(newLocationList: List<Location>)

    fun callThFinalBoss(): Animal

    fun generateAnimalByLocation(): Animal

    fun addListenerCurrentLocation(listenerCurrentLocation: CurrentLocationListener)

    fun addListenerLocation(commonLocationListener: LocationListener)

    fun restoreAnimalParameters()

    fun changeLocation(player: Player): Boolean

    fun equalizeCountOfItems(currentItemsSlot: ItemsSlot, newItemsForPlus: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>)

    fun setForCook(index: Int, itemsForCook: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int

    fun setAntiThief(index: Int, itemPhosphorus: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int

    fun setExtractWater(index: Int, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int

    fun setPlantFruit(index: Int, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>): Int

    fun growPlant(seedItem: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>)

    fun giveWaterForPlant(itemWater: ItemsSlot, backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>)

    fun destroyPlant(backpackByPlayer: MutableList<ItemsSlot>, newsList: MutableList<News>)

    fun startCooking(newsList: MutableList<News>)

    fun startAntiThief(newsList: MutableList<News>)

    fun startExtractingWater(newsList: MutableList<News>)

    fun startGrowingPlant(newsList: MutableList<News>)
}