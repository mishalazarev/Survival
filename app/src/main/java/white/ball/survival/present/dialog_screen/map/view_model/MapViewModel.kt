package white.ball.survival.present.dialog_screen.map.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.InteractionWithEnvironment
import white.ball.survival.domain.service.LocationListener
import white.ball.survival.domain.service.PlayerListener

class MapViewModel(
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironment: InteractionWithEnvironment
) : ViewModel() {

    private val _commonLocations = MutableLiveData<List<Location>>()
    val commonLocations: LiveData<List<Location>> = _commonLocations

    private val listenerCommonLocations: LocationListener = {
        _commonLocations.value = it
    }

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player

    private val playerListener: PlayerListener = {
        _player.value = it
    }

    init {
        interactionWithEnvironment.addListenerLocation(listenerCommonLocations)
        playerRepository.addPlayerListener(playerListener)
    }

    fun reloadMap() {
        onClickedLocationPressed(interactionWithEnvironment.getCurrentLocation())
    }

    fun onClickedLocationPressed(location: Location) {
        interactionWithEnvironment.onClickedLocationPressed(location)
    }

    fun travelInLocation(): Boolean {
        val result = interactionWithEnvironment.changeLocation(playerRepository.getPlayer())
        playerRepository.upDateInformation()
        return result
    }

    fun getCurrentLocation(): Location {
        return interactionWithEnvironment.getCurrentLocation()
    }

}