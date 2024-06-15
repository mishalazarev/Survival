package white.ball.survival.present.dialog_screen.lose

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.ColorFilter
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import white.ball.survival.R
import white.ball.survival.databinding.FragmentDefeatDialogBinding
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.present.dialog_screen.lose.view_model.DefeatViewModel
import white.ball.survival.present.screen.main_screen.game_play.fight.FightFragment.Companion.TAG
import white.ball.survival.present.view_model_factory.viewModelCreator

class DefeatDialogFragment private constructor() : DialogFragment() {

    private lateinit var binding: FragmentDefeatDialogBinding
    private val viewModel: DefeatViewModel by viewModelCreator {
        DefeatViewModel(
            it.playerAction,
            it.interactionWithEnvironment
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(requireContext(), R.layout.fragment_defeat_dialog, null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentDefeatDialogBinding.bind(view)

        dialog.setCancelable(false)

        with(binding) {

            val checkFlagOnArgument = arguments?.getBoolean(KEY_PLAYER_IS_ALIVE) ?: false
            if (checkFlagOnArgument) {
                val itemConstraintLayout =
                    View.inflate(requireContext(), R.layout.item_in_border_victory, null)
                val wrapperItemConstraintLayout =
                    itemConstraintLayout.findViewById<ConstraintLayout>(R.id.wrapper_item_constraint_layout)
                wrapperItemConstraintLayout.setBackgroundResource(R.drawable.border_item_lose)

                requireArguments().getInt(KEY_IMAGE_ITEM_RES_ID_PLAYER).let {
                    val itemImageView =
                        itemConstraintLayout.findViewById<ImageView>(R.id.image_item_drop_image_view)
                    itemImageView.setBackgroundResource(it)
                }

                val countTextView =
                    itemConstraintLayout.findViewById<TextView>(R.id.count_drop_text_view)
                countTextView.visibility = View.INVISIBLE

                exitToMainScreenButton.setOnClickListener {
                    dismiss()
                    if (requireArguments().getBoolean(KEY_PLAYER_IN_FIGHT_SCREEN)) {
                        findNavController().navigate(R.id.action_fightFragment_to_gamePlayFragment)
                    }
                }

                wrapperPanelInfoLinearLayout.addView(itemConstraintLayout)
            } else {
                captionSettingTextView.text = getText(R.string.caption_death)
                textInfoTextView.setText(R.string.text_defeat)

                decorLeftImageView.setImageResource(R.drawable.decor_death)
                decorRightImageView.setImageResource(R.drawable.decor_death)

                wrapperPanelInfoLinearLayout.setBackgroundResource(R.drawable.decor_panel_for_info_death)

                exitToMainScreenButton.setBackgroundColor(Color.parseColor("#272020"))

                exitToMainScreenButton.setOnClickListener {
                    dismiss()
                    if (requireArguments().getBoolean(KEY_PLAYER_IN_FIGHT_SCREEN)) {
                        findNavController().navigate(R.id.action_fightFragment_to_gamePlayFragment)
                        viewModel.travelInCemetery()
                        viewModel.clearPlayer()
                    } else {
                        viewModel.travelInCemetery()
                        viewModel.clearPlayer()
                    }
                }
            }
        }

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!
        dialog!!.setCanceledOnTouchOutside(false)
        with(window) {
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    companion object {

        fun newInstance(
            imageResId: Int,
            isAlive: Boolean,
            isFightScreen: Boolean
        ): DefeatDialogFragment =
            DefeatDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_IMAGE_ITEM_RES_ID_PLAYER, imageResId)
                    putBoolean(KEY_PLAYER_IS_ALIVE, isAlive)
                    putBoolean(KEY_PLAYER_IN_FIGHT_SCREEN, isFightScreen)
                }
            }

        @JvmStatic
        val KEY_IMAGE_ITEM_RES_ID_PLAYER = "key_image_item_res_id_player"

        @JvmStatic
        val KEY_PLAYER_IS_ALIVE = "key_player_is_alive"

        @JvmStatic
        val KEY_PLAYER_IN_FIGHT_SCREEN = "key_player_in_fight_screen"
    }

}