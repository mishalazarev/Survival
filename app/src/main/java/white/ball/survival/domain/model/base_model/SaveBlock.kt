package white.ball.survival.domain.model.base_model

import white.ball.survival.domain.model.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import white.ball.survival.present.screen.main_screen.game_play.GamePlayFragment.GameTimerController
import java.util.*

data class SaveBlock(
    val locations: List<Location>,
    val player: Player,
    val saveTime: GameTime,
)