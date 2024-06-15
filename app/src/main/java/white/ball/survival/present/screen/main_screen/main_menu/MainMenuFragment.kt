package white.ball.survival.present.screen.main_screen.main_menu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentMainMenuBinding
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.present.dialog_screen.create_new_game.CreateNewGameDialogFragment
import white.ball.survival.present.dialog_screen.process.ProcessDialogFragment
import white.ball.survival.present.screen.main_screen.game_play.GamePlayFragment
import white.ball.survival.present.screen.main_screen.main_menu.view_model.MainMenuViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator


class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    private lateinit var binding: FragmentMainMenuBinding

    private val createNewGame = CreateNewGameDialogFragment()
    private lateinit var mainThemeSound: MediaPlayer

    private lateinit var animationProcessDialogScreen: ProcessDialogFragment

    private lateinit var emptySaveGameSnackbar: Snackbar

    private val viewModel: MainMenuViewModel by viewModelCreator {
        MainMenuViewModel(
            it.progress,
            it.playerAction,
            it.interactionWithEnvironment,
            it.gameTimeAction
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainMenuBinding.bind(view)

        with(binding) {
            gamePlayTextViewLink.setOnClickListener{
                hidIconsOnSomeTime()
                navigator().onLaunchDialogFragmentPressed(createNewGame)
            }
            downloadGameTextViewLink.setOnClickListener{
                if (viewModel.getProgress()) {
                    hidIconsOnSomeTime()
                    lifecycleScope.launch {
                        animationProcessDialogScreen = ProcessDialogFragment.newInstance(false)
                        navigator().onLaunchDialogFragmentPressed(animationProcessDialogScreen)
                        delay(4_500)
                        findNavController().navigate(R.id.action_mainMenuFragment_to_gamePlayFragment)
                    }
                } else if (!emptySaveGameSnackbar.isShown) {
                    emptySaveGameSnackbar.show()
                }
            }
            exitTextViewLink.setOnClickListener{
                hidIconsOnSomeTime()
                requireActivity().finish()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        mainThemeSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.main_menu)
        mainThemeSound.start()
        mainThemeSound.isLooping = true

        val animationEyeStart = ObjectAnimator.ofPropertyValuesHolder(
            binding.whiteEyeImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        ).apply {
            duration = ANIMATION_EYE_TIME
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val animationReflectionDeathPerson = ObjectAnimator.ofPropertyValuesHolder(
            binding.deathPersonImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.2f)
        ).apply {
            duration = ANIMATION_REFLECTION_PERSON
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val animationPaw1 = ObjectAnimator.ofPropertyValuesHolder(
            binding.paw1ImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.3f)
        ).apply {
            duration = ANIMATION_PAW_ONE
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }
        animationPaw1.startDelay = ANIMATION_PAW_ONE

        val animationPaw2 = ObjectAnimator.ofPropertyValuesHolder(
            binding.paw2ImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.3f)
        ).apply {
            duration = ANIMATION_PAW_TWO
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }

        val animationPaw3 = ObjectAnimator.ofPropertyValuesHolder(
            binding.paw3ImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.4f)
        ).apply {
            duration = ANIMATION_PAW_THREE
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }

        val animationPaw4 = ObjectAnimator.ofPropertyValuesHolder(
            binding.paw4ImageView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.5f)
        ).apply {
            duration = ANIMATION_PAW_FOUR
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }

        AnimatorSet().apply {
            play(animationEyeStart)
                .after(animationReflectionDeathPerson)
                .after(animationPaw1)
                .after(animationPaw2)
                .after(animationPaw3)
                .after(animationPaw4)
            start()
        }

        emptySaveGameSnackbar = Snackbar.make(binding.root, R.string.empty_progress, Snackbar.LENGTH_SHORT)
    }

    override fun onStop() {
        super.onStop()
        mainThemeSound.stop()
        mainThemeSound.isLooping = false
        mainThemeSound.release()
    }
    private fun hidIconsOnSomeTime() {
        with(binding) {
            gamePlayTextViewLink.visibility = View.INVISIBLE
            downloadGameTextViewLink.visibility = View.INVISIBLE
            exitTextViewLink.visibility = View.INVISIBLE

            lifecycleScope.launch {
                delay(GamePlayFragment.SOME_TIME)
                gamePlayTextViewLink.visibility = View.VISIBLE
                downloadGameTextViewLink.visibility = View.VISIBLE
                exitTextViewLink.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        @JvmStatic
        val ANIMATION_EYE_TIME: Long = 5_000
        @JvmStatic
        val ANIMATION_REFLECTION_PERSON: Long = 30_000
        @JvmStatic
        val ANIMATION_PAW_ONE: Long = 21_000
        @JvmStatic
        val ANIMATION_PAW_TWO: Long = 19_000
        @JvmStatic
        val ANIMATION_PAW_THREE: Long = 17_000
        @JvmStatic
        val ANIMATION_PAW_FOUR: Long = 15_000
    }

}