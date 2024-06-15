package white.ball.survival.present.dialog_screen.backpack.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.convertor.UIRepository
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.service.*

class BackpackViewModel(
    private val uiRepository: UIRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository,
    private val playerAction: PlayerAction,
) : ViewModel() {

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player
    private val playerListener: PlayerListener = {
        _player.value = playerAction.getPlayer()
    }

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation
    private val currentLocationListener: CurrentLocationListener = {
        _currentLocation.value = interactionWithEnvironmentRepository.getCurrentLocation()
    }

    var currentBackpack: MutableLiveData<List<ItemsSlot>>? = null

    init {
        playerAction.addPlayerListener(playerListener)
        interactionWithEnvironmentRepository.addListenerCurrentLocation(currentLocationListener)
        currentBackpack?.value = playerAction.showMyBackpack()
    }

    fun getCurrentLocation(): Location {
        return interactionWithEnvironmentRepository.getCurrentLocation()
    }

    fun setBuild(itemBuild: ItemsSlot?){
        playerAction.setBuild(itemBuild)
    }

    fun takeFood(foods: ItemsSlot) {
        playerAction.takeFood(foods)
    }

    fun joinSimilarItemsSlot(itemsSlot: ItemsSlot) {
        playerAction.joinSimilarItemsSlot(itemsSlot)
    }

    fun openShell(itemSeed: ItemsSlot) {
        playerAction.openShell(itemSeed)
    }

    fun openScroll(itemScroll: ItemsSlot) {
        playerAction.openScroll(itemScroll)
    }

    fun upParameter(keyParameter: Char) {
        playerAction.upParameter(keyParameter)
    }

    fun divideIntoQualityGroups(itemsSlot: ItemsSlot) {
        playerAction.divideIntoQualityGroups(itemsSlot)
    }

    fun threwItAway(items: ItemsSlot) {
        playerAction.throwItemInCurrentBackpackAway(items)
    }

    fun threwAllItemsAway(items: ItemsSlot) {
        playerAction.throwAllItemsAway(items)
    }

    fun setPutOn(index: Int, itemsForPutOn: ItemsSlot?): Int {
        return playerAction.setPutOn(index, itemsForPutOn)
    }

    fun setAtiThief(index: Int, itemPhosphorus: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>): Int {
        return interactionWithEnvironmentRepository.setAntiThief(index, itemPhosphorus, backpackByPlayer, player.value!!.news)
    }

    fun setForCook(index: Int, itemsForCook: ItemsSlot?, backpackByPlayer: MutableList<ItemsSlot>): Int {
        return interactionWithEnvironmentRepository.setForCook(index, itemsForCook, backpackByPlayer, player.value!!.news)
    }

    fun setExtractWater(index: Int, backpackByPlayer: MutableList<ItemsSlot>): Int {
        return interactionWithEnvironmentRepository.setExtractWater(index, backpackByPlayer, player.value!!.news)
    }

    fun setPlantFruit(index: Int): Int {
        return interactionWithEnvironmentRepository.setPlantFruit(index, player.value!!.backpack, player.value!!.news)
    }

    fun growPlant(itemSeed: ItemsSlot) {
        interactionWithEnvironmentRepository.growPlant(itemSeed, player.value!!.backpack, player.value!!.news)
    }

    fun giveWaterForPlant(itemWater: ItemsSlot) {
        interactionWithEnvironmentRepository.giveWaterForPlant(itemWater, player.value!!.backpack, player.value!!.news)
    }

    fun destroyPlant() {
        interactionWithEnvironmentRepository.destroyPlant(player.value!!.backpack, player.value!!.news)
    }

    fun getIconForItemPutOnResImageMap(index: Int): Int {
        return uiRepository.getIconForItemResImage(index)
    }

}