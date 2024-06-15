package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.service.GameTimeListener

interface GameTimeRepository {

    fun loadGameTime()

    fun getCurrentGameTime(): GameTime

    fun setGameTime(newGameDayLeft: Int, gameTimeValue: Long)

    fun upDateInformation()

    fun addListenerGameTime(gameTimeListener: GameTimeListener)
}