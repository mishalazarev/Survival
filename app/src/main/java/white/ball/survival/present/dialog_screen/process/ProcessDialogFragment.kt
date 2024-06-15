package white.ball.survival.present.dialog_screen.process

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentProcessDialogBinding

class ProcessDialogFragment : DialogFragment(R.layout.fragment_process_dialog) {

    private lateinit var binding: FragmentProcessDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context,R.layout.fragment_process_dialog,null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentProcessDialogBinding.bind(view)

        if (requireArguments().getBoolean(IS_GAME_SAVING_FLAG_KEY)) binding.captionProcessTextView.setText(R.string.caption_save)

        lifecycleScope.launch {
            while (binding.playerHealthIndicatorProgressBar.progress != 100) {
                delay(250)
                binding.playerHealthIndicatorProgressBar.progress += 20
                delay(800)
                if (binding.playerHealthIndicatorProgressBar.progress == 100) dismiss()
            }
        }

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(false)
        val window = dialog!!.window!!

        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    companion object {
        @JvmStatic
        val IS_GAME_SAVING_FLAG_KEY = "is_download_flag_key"

        fun newInstance(isGameSaving: Boolean): ProcessDialogFragment = ProcessDialogFragment().apply{
            arguments = Bundle().apply {
                putBoolean(IS_GAME_SAVING_FLAG_KEY, isGameSaving)
            }
        }
    }

}