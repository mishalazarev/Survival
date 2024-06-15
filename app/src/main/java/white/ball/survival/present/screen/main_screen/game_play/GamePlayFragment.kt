package white.ball.survival.present.screen.main_screen.game_play

import android.animation.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentGamePlayBinding
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.domain.repository.GameTimerControllerRepository
import white.ball.survival.domain.repository.Plant
import white.ball.survival.present.dialog_screen.backpack.BackpackDialogFragment
import white.ball.survival.present.dialog_screen.build.bonfire.BonfireDialogFragment
import white.ball.survival.present.dialog_screen.build.house.HouseDialogFragment
import white.ball.survival.present.dialog_screen.build.well.WellDialogFragment
import white.ball.survival.present.dialog_screen.create.CreateRecipeItemDialogFragment
import white.ball.survival.present.dialog_screen.lose.DefeatDialogFragment
import white.ball.survival.present.dialog_screen.map.MapDialogFragment
import white.ball.survival.present.dialog_screen.notification.NotificationDialogFragment
import white.ball.survival.present.dialog_screen.process.ProcessDialogFragment
import white.ball.survival.present.dialog_screen.user_guide.adapter.UserGuideDialogFragment
import white.ball.survival.present.screen.main_screen.game_play.view_model.GamePlayViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class GamePlayFragment() : Fragment() {

    private lateinit var binding: FragmentGamePlayBinding

    private lateinit var gameTimerController: GameTimerController

    private val create = CreateRecipeItemDialogFragment()
    private val backpack = BackpackDialogFragment()
    private val map = MapDialogFragment()
    private val userGuide = UserGuideDialogFragment()
    private val notification: NotificationDialogFragment by lazy { NotificationDialogFragment() }

    private val bonfire = BonfireDialogFragment()
    private val well = WellDialogFragment()
    private val house = HouseDialogFragment()

    private lateinit var animationProcessDialogScreen: ProcessDialogFragment

    private lateinit var gamePlaySound: MediaPlayer

    private lateinit var sleepSnackbar: Snackbar

    private val viewModel: GamePlayViewModel by viewModelCreator {
        GamePlayViewModel(
            playerRepository = it.playerAction,
            interactionWithEnvironmentRepository = it.interactionWithEnvironment,
            gameTimeRepository = it.gameTimeAction,
            progress = it.progress,
            it.musicController
        )
    }

    private val drawableIndicatorHealthDefault by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.indicator_health_default
        )
    }
    private val drawableIndicatorHealthPoison by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.indicator_health_poison
        )
    }

    private val drawableIndicatorEnduranceDefault by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.indicator_endurance_default
        )
    }
    private val drawableIndicatorEnduranceWeak by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.indicator_endurance_weak
        )
    }

    private var plantMonster: Plant? = null

    private val textHintArray = arrayOf(
        "Добро пожаловать в Survival, позвольте вам не много объяснить основные моменты.",
        "Это параметры вашего персонажа: здоровье, выносливость, сытость и жажда.",
        "При равном 0% сытости у вас будут отниматься жизни, а при выше 70% жизни будут восстанавливаться.",
        "Уровень жажды при 0%, то у вас не будет восстанавливаться выносливость.",
        "В случае если вы захотите спать раньше ночи, то нажимайте на этот значок, он работает не всегда, но это все лучше чем ничего. " +
                "\nПеред сном желательно и повесить фосфор, иначе воры незаставять себя ждать.",
        "Вся информация об растениях, животных и новых обновлениях находится здесь.",
        "Когда что-то происходит вы всегда можете узнать об этом здесь.",
        "Что-бы выжить в этих суровых местам вам нужно строить здания, оружия и доспехи.",
        "А для строительства потребуются ресурсы, которые можно получить у противников, но за просто так они вам их не отдадут.",
        "Это ваши здание, но пока здесь еще ничего нет, но все ещё впереди.\nПостроить можно ТОЛЬКО ОДНО ЗДАНИЕ В ЛОКАЦИИ!",
        "Это карта, которая позволит вам перемещаться между локациями, но на начальном уровня я бы не рисковал этого делать. Чтобы перейти на другую локацию потребуется 25 вынослиовсти.",
        "Тут вся ваша добыча и основные характеристики. Не забывайте использовать очки умения.",
        "Я думаю тебе этого хватит на первый раз.\nЕсли что я всегда буду рядом."
    )
    private lateinit var animateActionIdList: MutableList<View?>

    private lateinit var animateIcons: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamePlayBinding.inflate(inflater, container, false)

        if (viewModel.player.value!!.isHintsInGamePlayIncluded) {
            binding.hintTextView.visibility = View.VISIBLE

            binding.hintTextView.text = textHintArray[viewModel.countHint]
            viewModel.countHint += 1

            binding.hintTextView.setOnClickListener {
                if (textHintArray.size == viewModel.countHint) {
                    binding.hintTextView.visibility = View.INVISIBLE
                    viewModel.player.value!!.isHintsInGamePlayIncluded = false
                } else {
                    binding.hintTextView.text = textHintArray[viewModel.countHint]
                    alphaAnimationOfIcon(viewModel.countHint)
                    viewModel.countHint += 1
                }
            }


        }

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            binding.backgroundGamePlayConstraintLayout.setBackgroundResource(it.imageResId)
        }

        with(binding) {
            createImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                launchDialogFragment(create) }
            // change
            backpackImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                launchDialogFragment(backpack) }
            mapImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                launchDialogFragment(map) }
            clueImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                launchDialogFragment(userGuide) }
            notificationImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                launchDialogFragment(notification) }
            lookingForAnEnemyImageButton.setOnClickListener {
                lookingForEnemy()
            }

            binding.sleepImageButton.setOnClickListener {
                sleepSnackbar.show()
            }
        }

        viewModel.player.observe(viewLifecycleOwner) {
            with(binding) {
                reloadInformationByPlayer()
                if (it.satietyAndThirstSettingFlag) {
                    indicatorFoodTextView.text = "${it.satiety.indicator.percent} %"
                    indicatorWaterTextView.text = "${it.thirst.indicator.percent} %"
                } else {
                    wrapperMainIndicatorsConstraintLayout.removeView(foodImageView)
                    wrapperMainIndicatorsConstraintLayout.removeView(waterImageView)
                    wrapperMainIndicatorsConstraintLayout.removeView(indicatorFoodTextView)
                    wrapperMainIndicatorsConstraintLayout.removeView(indicatorWaterTextView)
                }
                if (it.health.isPoisoning) {
                    healthIndicatorProgressBar.background = drawableIndicatorHealthPoison
                } else {
                    healthIndicatorProgressBar.background = drawableIndicatorHealthDefault
                }

                if (it.endurance.isWeakYet) {
                    enduranceIndicatorProgressBar.background = drawableIndicatorEnduranceWeak
                } else {
                    enduranceIndicatorProgressBar.background = drawableIndicatorEnduranceDefault
                }
            }

        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        gamePlaySound = MediaPlayer.create(requireActivity().applicationContext, R.raw.game_play)
        gamePlaySound.isLooping = true
        gamePlaySound.start()

        gameTimerController = GameTimerController(viewModel.gameTime.value)
        gameTimerController.startGameTime()

        sleepSnackbar = Snackbar.make(requireView(), R.string.caption_are_sure_sleep, Snackbar.LENGTH_SHORT)
        sleepSnackbar.setAction(R.string.sleep) {
            gameTimerController.stopGameTime()
            val currentGameTIme = viewModel.gameTime.value
            currentGameTIme!!.gameTimeValue = DAY_END_TIME
            gameTimerController = GameTimerController(currentGameTIme)
            gameTimerController.startGameTime()
            gameTimerController.sleep()
        }
        sleepSnackbar.let {
            it.setActionTextColor(requireContext().getColor(R.color.yellow_light))
            it.setTextColor(requireContext().getColor(R.color.gray))
            it.setBackgroundTint(requireContext().getColor(R.color.head_create_new_game_dialog_fragment))
        }
    }

    override fun onStop() {
        super.onStop()
        gamePlaySound.stop()
        gamePlaySound.isLooping = false
        gamePlaySound.release()
        gameTimerController.stopGameTime()
    }

    private fun lookingForEnemy() {
        hidIconsOnSomeTime()
        gameTimerController.gameTime.cancel()
        findNavController().navigate(R.id.action_gamePlayFragment_to_fightFragment)
    }

    private fun alphaAnimationOfIcon(indexIcon: Int) {
        animateActionIdList = mutableListOf(null, binding.wrapperMainIndicatorsConstraintLayout, binding.foodImageView, binding.waterImageView, binding.sleepImageButton,
            binding.clueImageButton, binding.notificationImageButton, binding.createImageButton, binding.lookingForAnEnemyImageButton, binding.buildImageButton,
            binding.mapImageButton, binding.backpackImageButton, null)

        if (animateActionIdList[indexIcon - 1] != null) {
            animateIcons.cancel()
            animateActionIdList[indexIcon - 1]!!.alpha = 1f
        }

        if (animateActionIdList[indexIcon] != null) {
            val animationAlphaIcon = ObjectAnimator.ofPropertyValuesHolder(
                animateActionIdList[indexIcon],
                PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
            ).apply {
                duration = ANIMATE_ALPHA_ICON
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                start()
            }

            animateIcons = AnimatorSet().apply {
                play(animationAlphaIcon)
                start()
            }
        }
    }


    private fun loadBuildInLocation() {
        val currentBuildInLocation = viewModel.currentLocation.value!!.build
        binding.buildImageButton.isEnabled = currentBuildInLocation != null

        binding.buildImageButton.setOnClickListener {
            hidIconsOnSomeTime()
            when (currentBuildInLocation!!.nameItem) {
                R.string.bonfire_recipe_item -> {
                    launchDialogFragment(bonfire)
                }
                R.string.well_build_recipe_item -> {
                    launchDialogFragment(well)
                }
                else -> {

                    viewModel.currentLocation.value!!.build?.plant?.let {
                        plantMonster = it
                    }

                    if (plantMonster != null && plantMonster!!.currentLevel == plantMonster!!.commonProcess.size - 1 && plantMonster!!.ability == PlantAbility.MONSTER) {
                        lookingForEnemy()
                    } else {
                        launchDialogFragment(house)
                    }
                }
            }

        }
    }


    private fun launchDialogFragment(dialogFragment: DialogFragment) {
        navigator().onLaunchDialogFragmentPressed(dialogFragment)
    }

    private fun changeIconNotification() {
        binding.notificationImageButton.isActivated = viewModel.getCurrentNewsSize() != 0
    }

    private fun reloadInformationByPlayer() {
        viewModel.player.observe(viewLifecycleOwner) {
            with(binding) {
                healthIndicatorProgressBar.setMaxValue(it.health.indicator.maxPercent)
                healthIndicatorProgressBar.setValue(it.health.indicator.percent)
                enduranceIndicatorProgressBar.setMaxValue(it.endurance.indicator.maxPercent)
                enduranceIndicatorProgressBar.setValue(it.endurance.indicator.percent)
            }
        }
    }

    private fun hidIconsOnSomeTime() {
        with(binding) {
            createImageButton.visibility = View.INVISIBLE
            buildImageButton.visibility = View.INVISIBLE
            backpackImageButton.visibility = View.INVISIBLE
            mapImageButton.visibility = View.INVISIBLE
            notificationImageButton.visibility = View.INVISIBLE
            clueImageButton.visibility = View.INVISIBLE
            lookingForAnEnemyImageButton.visibility = View.INVISIBLE
            sleepImageButton.visibility = View.INVISIBLE

            lifecycleScope.launch {
                delay(SOME_TIME)
                createImageButton.visibility = View.VISIBLE
                buildImageButton.visibility = View.VISIBLE
                backpackImageButton.visibility = View.VISIBLE
                mapImageButton.visibility = View.VISIBLE
                notificationImageButton.visibility = View.VISIBLE
                clueImageButton.visibility = View.VISIBLE
                lookingForAnEnemyImageButton.visibility = View.VISIBLE
                sleepImageButton.visibility = View.VISIBLE
            }
        }
    }

    inner class GameTimerController(gameTime: GameTime?) : GameTimerControllerRepository {

        override lateinit var gameTime: CountDownTimer
        override var gameDaysLeft = 0
        override var elapsedTime: Long = 0
        private var checkOnRepeatAboutSleepSoonBoolean = true
        private var checkOnRepeatAboutYouCanSleep = true

        private val effectsByPlayer = viewModel.player.value!!.effectHaveDeny
        private var indexByPlayerEffects = 0

        private val animationAlphaOn = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        private val animationAlphaOff = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)

        init {
            gameTime?.also {
                gameDaysLeft = it.gameDaysLeft
                elapsedTime = it.gameTimeValue
            }
        }

        override fun startGameTime() {
//            binding.dayTextView.text = gameDaysLeft.toString()
            with(binding) {
                gameTime = object : CountDownTimer(elapsedTime, SECOND) {
                    override fun onTick(seconds: Long) {
//                        timeTextView.text = "${seconds / SECOND}"
                        elapsedTime = seconds

                        binding.haveEffectPlayerLinearLayout.removeAllViews()
                        while (effectsByPlayer.size != indexByPlayerEffects) {
                            if (effectsByPlayer.size < indexByPlayerEffects) indexByPlayerEffects = 0
                            if(effectsByPlayer.size == 0) {
                                Effect.values().forEach {
                                    it.timeAction = 90_000L
                                }
                                break
                            }

                            val imageEffect = ImageView(requireContext())
                            imageEffect.setImageResource(effectsByPlayer[indexByPlayerEffects].imageEffectResId)
                            binding.haveEffectPlayerLinearLayout.addView(imageEffect)
                            indexByPlayerEffects++
                        }
                        if (effectsByPlayer.size <= indexByPlayerEffects) indexByPlayerEffects = 0

                        if (seconds / SECOND == 30L && checkOnRepeatAboutSleepSoonBoolean) {
                            Toast.makeText(
                                activity,
                                R.string.warning_about_sleep,
                                Toast.LENGTH_SHORT
                            ).show()
                            checkOnRepeatAboutSleepSoonBoolean = false
                        } else if (seconds / SECOND == TIME_FOR_SLEEP && checkOnRepeatAboutYouCanSleep) {
                            Toast.makeText(
                                activity,
                                R.string.warning_about_you_can_sleep,
                                Toast.LENGTH_SHORT
                            ).show()
                            checkOnRepeatAboutYouCanSleep = false
                        }

                        if (viewModel.gameTime.value!!.gameSaveFlag) {
                            viewModel.gameTime.value!!.gameSaveFlag = false
                            gameSaving()
                        }

                        viewModel.setGameTimeParameters(gameDaysLeft, elapsedTime)

                        changeIconNotification()
                        reloadInformationByPlayer()

                        viewModel.startCooking()
                        viewModel.startAntiThief()
                        viewModel.startExtractingWater()
                        viewModel.startGrowingPlant()

                        binding.sleepImageButton.isEnabled = seconds / SECOND <= TIME_FOR_SLEEP
                        loadBuildInLocation()

                        if (elapsedTime.toInt() % TIME_UP_DATE_PHYSIOLOGICAL == 0) {
                            viewModel.upDatePhysiologicalParameters()
                        }

                        if (viewModel.player.value!!.health.indicator.percent <= 0) {
                            gameTime.cancel()
                            navigator().onLaunchDialogFragmentPressed(
                                DefeatDialogFragment.newInstance(
                                    0,
                                    false,
                                    false
                                )
                            )
                        }

                        if (viewModel.gameTime.value!!.playerAlreadySleep) {
                            viewModel.setGameTimeParameters(gameDaysLeft, 0)
                            viewModel.gameTime.value!!.playerAlreadySleep = false
                        }

                    }

                    override fun onFinish() {
                        sleep()
                        startGameTime()
                    }

                }.start()
            }
        }

        override fun stopGameTime() {
            gameTime.cancel()
        }

        override fun getGameTimeValueNow(): Long = elapsedTime

        override fun sleep() {
            with(binding) {
                createImageButton.visibility = View.INVISIBLE
                buildImageButton.visibility = View.INVISIBLE
                backpackImageButton.visibility = View.INVISIBLE
                mapImageButton.visibility = View.INVISIBLE
                notificationImageButton.visibility = View.INVISIBLE
                clueImageButton.visibility = View.INVISIBLE
                lookingForAnEnemyImageButton.visibility = View.INVISIBLE
                sleepImageButton.visibility = View.INVISIBLE

                backgroundShadowImageView.visibility = View.VISIBLE

                gameDaysLeft -= 1
                elapsedTime = DAY_END_TIME
                checkOnRepeatAboutSleepSoonBoolean = true
                checkOnRepeatAboutYouCanSleep = true
//                dayTextView.text = gameDaysLeft.toString()

                val animationBackgroundAppearance = ObjectAnimator.ofPropertyValuesHolder(
                    backgroundShadowImageView,
                    animationAlphaOn
                ).apply {
                    duration = TIME_ANIMATION_SLEEP
                    interpolator = AnticipateInterpolator()
                }

                val animationBackgroundSleepDisappearance = ObjectAnimator.ofPropertyValuesHolder(
                    backgroundShadowImageView, animationAlphaOff
                ).apply {
                    duration = TIME_ANIMATION_SLEEP
                    interpolator = AnticipateInterpolator()
                }

                AnimatorSet().apply {
                    play(animationBackgroundAppearance).before(
                        animationBackgroundSleepDisappearance
                    )
                    start()
                }

                viewModel.player.value!!.also {
                    it.health.indicator.percent = it.health.indicator.maxPercent
                    it.endurance.indicator.percent = it.endurance.indicator.maxPercent
                }

                viewModel.thiefStealItem()

                lifecycleScope.launch {
                    delay(TIME_ANIMATION_SLEEP)
                    gameSaving()
                    createImageButton.visibility = View.VISIBLE
                    buildImageButton.visibility = View.VISIBLE
                    backpackImageButton.visibility = View.VISIBLE
                    mapImageButton.visibility = View.VISIBLE
                    notificationImageButton.visibility = View.VISIBLE
                    clueImageButton.visibility = View.VISIBLE
                    lookingForAnEnemyImageButton.visibility = View.VISIBLE
                    sleepImageButton.visibility = View.VISIBLE
                }
            }
        }

        private fun gameSaving() {
            viewModel.saveProgress()
            animationProcessDialogScreen = ProcessDialogFragment.newInstance(true)
            navigator().onLaunchDialogFragmentPressed(animationProcessDialogScreen)
        }
    }

    companion object {
        @JvmStatic
        val SECOND: Long = 1_000
        @JvmStatic
        val ANIMATE_ALPHA_ICON: Long = 1_500
        @JvmStatic
        val SOME_TIME: Long = 250
        @JvmStatic
        val TIME_ANIMATION_SLEEP: Long = 8_000
        @JvmStatic
        val DAY_END_TIME: Long = 380_000
        @JvmStatic
        val TIME_UP_DATE_PHYSIOLOGICAL = 20
        @JvmStatic
        val TIME_FOR_SLEEP = 250L
    }

}