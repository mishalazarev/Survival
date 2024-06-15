package white.ball.survival.present.dialog_screen.create_new_game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import white.ball.survival.R
import white.ball.survival.databinding.FragmentCreateNewGameBinding
import white.ball.survival.present.screen.main_screen.game_play.view_model.GamePlayViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class CreateNewGameDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentCreateNewGameBinding

    private lateinit var emptyNameSnackbar : Snackbar

    private val viewModel: GamePlayViewModel by viewModelCreator {
        GamePlayViewModel(
            playerRepository = it.playerAction,
            interactionWithEnvironmentRepository = it.interactionWithEnvironment,
            gameTimeRepository = it.gameTimeAction,
            progress = it.progress,
            musicController = it.musicController
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_create_new_game, null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentCreateNewGameBinding.bind(view)
        var isTurnSatietyAndThirstBoolean = true
        var isTurnHintsBoolean = true

        with(binding) {

            turnSatietyThirstButton.setOnClickListener {
                isTurnSatietyAndThirstBoolean = changeText(turnSatietyThirstButton.text.toString()) == TURN_ON_TEXT
                turnSatietyThirstButton.text = changeText(turnSatietyThirstButton.text.toString())
            }

            hintsIncludedButton.setOnClickListener {
                isTurnHintsBoolean = changeText(hintsIncludedButton.text.toString()) == TURN_ON_TEXT
                hintsIncludedButton.text = changeText(hintsIncludedButton.text.toString())
            }

            createNewGameButton.setOnClickListener {
                if (namePlayerEditText.text.isNotBlank()) {
                    viewModel.clearProgress()
                    viewModel.loadPlayer(namePlayerEditText.text.toString(), isTurnSatietyAndThirstBoolean, isTurnHintsBoolean)
                    findNavController().navigate(R.id.action_mainMenuFragment_to_gamePlayFragment)
                    dismiss()
                } else if (!emptyNameSnackbar.isShown) {
                    emptyNameSnackbar.show()
                }
            }

            exitTextView.setOnClickListener {
                dismiss()
            }
        }

        return dialog
            .setView(view)
            .create()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!

        emptyNameSnackbar = Snackbar.make(binding.root, R.string.the_name_player_is_empty, Snackbar.LENGTH_SHORT)
        with(window) {
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun changeText(textInButton: String): String {
        return if (textInButton == TURN_ON_TEXT) {
            TURN_OFF_TEXT
        } else {
            TURN_ON_TEXT
        }
    }

    companion object {

        @JvmStatic
        val TURN_ON_TEXT = "Вкл."

        @JvmStatic
        val TURN_OFF_TEXT = "выкл."

    }

}