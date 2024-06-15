package white.ball.survival.present.dialog_screen.lose.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.repository.InteractionWithEnvironmentRepository
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.PlayerListener

class DefeatViewModel(
    private val playerRepository: PlayerRepository,
    private val interactionWithEnvironmentRepository: InteractionWithEnvironmentRepository
) : ViewModel() {

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player
    private val playerListener: PlayerListener = {
        _player.value = it
    }

    init {
        playerRepository.addPlayerListener(playerListener)
    }

    fun clearPlayer() = playerRepository.death()

    fun travelInCemetery() {
        interactionWithEnvironmentRepository.onClickedLocationPressed(interactionWithEnvironmentRepository.getCommonLocation()[5])
        interactionWithEnvironmentRepository.changeLocation(playerRepository.getPlayer())
    }

}