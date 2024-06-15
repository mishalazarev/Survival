package white.ball.survival.domain.model.base_model

import androidx.room.ColumnInfo
import androidx.room.Entity

data class GameTime(
    var gameTimeValue: Long = 380_000,
    var gameDaysLeft: Int = 22,
    var gameSaveFlag: Boolean = false,
    var playerAlreadySleep: Boolean = false
)