package white.ball.survival.present.dialog_screen.victory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import white.ball.survival.R
import white.ball.survival.databinding.FragmentVictoryDialogBinding
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.present.dialog_screen.victory.adapter.DropAdapter
import white.ball.survival.present.dialog_screen.victory.view_model.VictoryViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class VictoryDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentVictoryDialogBinding

    private val adapter = DropAdapter()
    private val viewModel: VictoryViewModel by viewModelCreator {
        VictoryViewModel(
            it.playerAction,
            it.interactionWithEnvironment
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_victory_dialog, null)
        binding = FragmentVictoryDialogBinding.bind(view)
        val dialog = AlertDialog.Builder(activity)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        arguments?.getInt(KEY_NAME_ANIMAL)?.let {
            viewModel.getCurrentAnimal(it)
        }

        viewModel.loadAnimal()
        dialog.setCancelable(false)

        with(binding) {
            dropRecyclerView.layoutManager = linearLayoutManager
            dropRecyclerView.adapter = adapter

            val dropItems = viewModel.getDropItems()
            val dropExp = viewModel.getExp()
            expTextView.text = "+$dropExp"
            adapter.itemsDropList = dropItems
            viewModel.takeDropByPlayer(dropItems.toMutableList(), dropExp)

            exitToMainScreenButton.setOnClickListener {
                dismiss()
                viewModel.restoreAnimalParameters()
                viewModel.loadAnimal()
                findNavController().navigate(R.id.action_fightFragment_to_gamePlayFragment)
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

        fun newInstance(nameAnimal: Int): VictoryDialogFragment = VictoryDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_NAME_ANIMAL, nameAnimal)
            }
        }

        @JvmStatic
        val KEY_NAME_ANIMAL = "key_name_animal"
    }
}