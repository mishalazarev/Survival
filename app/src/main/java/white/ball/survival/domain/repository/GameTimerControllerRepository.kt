package white.ball.survival.domain.repository

import android.os.CountDownTimer
import white.ball.survival.domain.model.base_model.GameTime

interface GameTimerControllerRepository {

    var gameTime: CountDownTimer
    var elapsedTime: Long
    var gameDaysLeft: Int

    fun stopGameTime()
    fun sleep()
    fun getGameTimeValueNow(): Long
    fun startGameTime()
}
