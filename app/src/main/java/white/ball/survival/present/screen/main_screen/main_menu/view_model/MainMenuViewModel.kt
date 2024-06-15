package white.ball.survival.present.screen.main_screen.main_menu.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.repository.GameTimeRepository
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.CurrentLocationListener
import white.ball.survival.domain.service.GameTimeListener
import white.ball.survival.domain.service.PlayerListener
import white.ball.survival.domain.service.Progress

class MainMenuViewModel(
    private val progress: Progress,
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository,
    private val gameTimeRepository: GameTimeRepository
) : ViewModel() {

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player
    private val playerListener: PlayerListener = {
        _player.value = playerRepository.getPlayer()
    }

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation
    private val currentLocationListener: CurrentLocationListener = {
        _currentLocation.value = it
    }

    private val _gameTime = MutableLiveData<GameTime>()
    val gameTime: LiveData<GameTime> = _gameTime
    private val gameTimeValueListener: GameTimeListener = {
        _gameTime.value = it
    }

    init {
        playerRepository.loadPlayer(Player())
        playerRepository.addPlayerListener(playerListener)
        gameTimeRepository.loadGameTime()
        interactionWithEnvironmentRepository.addListenerCurrentLocation(currentLocationListener)
        gameTimeRepository.addListenerGameTime(gameTimeValueListener)
    }

    fun getProgress(): Boolean {
        val progressGameTime = progress.getGameTimeProgress()
        val progressPlayer = progress.getPlayerProgress()

        if (progressPlayer.level == 0) return false

        playerRepository.loadPlayer(progressPlayer)
        interactionWithEnvironmentRepository.setCommonLocation(progress.getLocationListProgress())
        interactionWithEnvironmentRepository.loadMap(progress.getLocationListProgress())
        gameTimeRepository.setGameTime(progressGameTime.gameDaysLeft, progressGameTime.gameTimeValue)

        return true
    }
}