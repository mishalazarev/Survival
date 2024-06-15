package white.ball.survival.domain.service

import android.view.View
import android.widget.Chronometer
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.repository.GameTimeRepository

typealias GameTimeListener = (currentGameTime: GameTime) -> Unit

class GameTimeAction(
    private val gameTime: GameTime
) : GameTimeRepository {

    private lateinit var currentGameTime: GameTime
    private val listenersGameTime = mutableListOf<GameTimeListener>()

    override fun loadGameTime() {
        currentGameTime = gameTime
    }

    override fun getCurrentGameTime(): GameTime = currentGameTime

    override fun setGameTime(newGameDayLeft: Int, gameTimeValue: Long) {
        currentGameTime.gameDaysLeft = newGameDayLeft
        currentGameTime.gameTimeValue = gameTimeValue
        notifyGameTimeChanges()
    }

    override fun upDateInformation() {
        notifyGameTimeChanges()
    }

    override fun addListenerGameTime(gameTimeListener: GameTimeListener) {
        listenersGameTime.add(gameTimeListener)
        gameTimeListener.invoke(currentGameTime)
    }

    private fun notifyGameTimeChanges() {
        listenersGameTime.forEach { it.invoke(currentGameTime) }
    }

}