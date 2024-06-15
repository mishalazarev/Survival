package white.ball.survival.domain.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import white.ball.survival.domain.convertor.UIRepository
import white.ball.survival.domain.service.InteractionWithEnvironment
import white.ball.survival.domain.service.PlayerAction
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.model.music_controller.MusicController
import white.ball.survival.domain.service.GameTimeAction
import white.ball.survival.domain.service.Progress
import white.ball.survival.domain.service.Progress.Companion.GAME_PLAY_PROGRESS_FILE

class MainApp : Application() {

    private lateinit var gamePlayProgress: SharedPreferences
    private lateinit var player: Player
    private lateinit var gameTime: GameTime

    lateinit var uiAction: UIAction
    lateinit var playerAction: PlayerAction
    lateinit var interactionWithEnvironment: InteractionWithEnvironment
    lateinit var gameTimeAction: GameTimeAction

    private lateinit var locationList: List<Location>

    val progress = Progress()
    val musicController = MusicController()

    override fun onCreate() {
        super.onCreate()
        gamePlayProgress = applicationContext.getSharedPreferences(GAME_PLAY_PROGRESS_FILE, MODE_PRIVATE)
        progress.init(gamePlayProgress)

        player = Player()
        locationList = Location.values().toList()
        gameTime = GameTime()
        uiAction = UIAction(applicationContext)

        gameTimeAction = GameTimeAction(
            gameTime
        )

        interactionWithEnvironment = InteractionWithEnvironment(
            uiAction,
            locationList = locationList
        )

        playerAction = PlayerAction(
            uiAction,
            interactionWithEnvironment,
            player
        )
    }

    class UIAction(
        private val appContext: Context
    ) : UIRepository {

        override fun getString(resId: Int): String {
            return appContext.getString(resId)
        }

        override fun getIconForItemResImage(index: Int): Int = when (index) {
            0 -> PlayerAction.ICON_FOR_ITEM_WEAPON
            1 -> PlayerAction.ICON_FOR_ITEM_ARROW
            2 -> PlayerAction.ICON_FOR_ITEM_ARMOR
            3 -> PlayerAction.ICON_FOR_ITEM_SCROLL
            4 -> PlayerAction.ICON_FOR_ITEM_MEAT
            5 -> PlayerAction.ICON_FOR_ITEM_WOOD
            6 -> 0 //
            7 -> PlayerAction.ICON_FOR_ITEM_GEM
            8 -> 0 // GEM_DIM
            9 -> 0 // place for extract water
            10 -> 0 // place for fruit of plant
            else -> throw IllegalArgumentException("bounds of exception")
        }

        override fun getContext(): Context = appContext
    }


}



