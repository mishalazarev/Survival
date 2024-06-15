package white.ball.survival.present.screen.main_screen.game_play.view_model

import white.ball.survival.domain.model.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.R
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.extension_model.IndicatorThirst
import white.ball.survival.domain.model.music_controller.MusicController
import white.ball.survival.domain.service.*
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.repository.GameTimeRepository

class GamePlayViewModel(
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository,
    private val gameTimeRepository: GameTimeRepository,
    private val progress: Progress,
    private val musicController: MusicController
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

    var countHint = 0

    init {
        with(playerRepository) {
            addPlayerListener(playerListener)
        }
        gameTimeRepository.loadGameTime()
        interactionWithEnvironmentRepository.addListenerCurrentLocation(currentLocationListener)
        gameTimeRepository.addListenerGameTime(gameTimeValueListener)
    }

    fun loadPlayer(namePlayer: String, flagSatietyAndThirst: Boolean, flagHints: Boolean) {
        playerRepository.loadPlayer(Player(namePlayer, flagSatietyAndThirst, flagHints, flagHints))
    }

    fun getCurrentNewsSize(): Int = playerRepository.showNewsList().size

    fun thiefStealItem() {
        playerRepository.thiefStealItem()
    }

    fun upDatePhysiologicalParameters(): Boolean {
        val result = playerRepository.isUpDatePhysiologicalParameters()
        playerRepository.upDateInformation()
        return result
    }

    fun clearProgress() {
        progress.clearProgress()
    }

    fun saveProgress() {
        progress.saveProgress(
            newLocations = interactionWithEnvironmentRepository.getCommonLocation(),
            newProgressPlayer = playerRepository.getPlayer(),
            newGameTime = gameTimeRepository.getCurrentGameTime()
        )
    }

    fun setGameTimeParameters(newGameDaysLeft: Int, gameTimeValue: Long){
        gameTimeRepository.setGameTime(newGameDaysLeft, gameTimeValue)
        gameTimeRepository.upDateInformation()
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