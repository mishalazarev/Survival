package white.ball.survival.present.dialog_screen.notification.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.repository.PlayerRepository
import white.ball.survival.domain.service.PlayerListener

class NotificationViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player
    private val playerListener: PlayerListener = {
        _player.value = playerRepository.getPlayer()
    }

    init {
        playerRepository.addPlayerListener(playerListener)
    }

    fun getNewsList(): List<News> = playerRepository.showNewsList()

    fun clearNewsList() {
        playerRepository.clearNewsList()
    }

}