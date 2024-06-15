package white.ball.survival.present.dialog_screen.end_game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import white.ball.survival.R
import white.ball.survival.databinding.FragmentEndGameDialogBinding
import white.ball.survival.present.screen.main_screen.game_play.view_model.GamePlayViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class EndGameDialogFragment : DialogFragment(R.layout.fragment_end_game_dialog) {

    private lateinit var binding: FragmentEndGameDialogBinding

    private val viewModel: GamePlayViewModel by viewModelCreator {
        GamePlayViewModel(
            it.playerAction,
            it.interactionWithEnvironment,
            it.gameTimeAction,
            it.progress,
            it.musicController
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_end_game_dialog, null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentEndGameDialogBinding.bind(view)

        with(binding) {
            if (requireArguments().getBoolean(IS_LOSE_GAME_KEY)) {
                captionEndGameTextView.setText(R.string.caption_lose_end_game)
                leftIconImageView.setImageResource(R.drawable.decor_death)
                rightIconImageView.setImageResource(R.drawable.decor_death)
            }


            endGameButton.setOnClickListener {
                dismiss()
                viewModel.clearProgress()
                findNavController().navigate(R.id.action_fightFragment_to_mainMenuFragment)
            }
        }

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }

    companion object {
        @JvmStatic
        val IS_LOSE_GAME_KEY = "is_win_game_key"

        fun newInstance(isLoseGame: Boolean): EndGameDialogFragment =
            EndGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_LOSE_GAME_KEY, isLoseGame)
                }
            }
    }

}