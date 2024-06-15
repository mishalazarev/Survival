package white.ball.survival.present.dialog_screen.build.well

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentWellDialogBinding
import white.ball.survival.present.dialog_screen.backpack.view_model.BackpackViewModel
import white.ball.survival.present.dialog_screen.build.bonfire.BonfireDialogFragment
import white.ball.survival.present.view_model_factory.viewModelCreator

class WellDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentWellDialogBinding

    private lateinit var antiThiefStartJob: Job
    private var antiThiefFinishBoolean = true

    private lateinit var waterStartJob: Job
    private var waterFinishBoolean = true

    private val viewModel: BackpackViewModel by viewModelCreator {
        BackpackViewModel(
            it.uiAction,
            it.interactionWithEnvironment,
            it.playerAction
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_well_dialog, null)
        binding = FragmentWellDialogBinding.bind(view)
        binding.loadAntiThiefProgressBar.max = viewModel.currentLocation.value!!.build!!.antiThief!!.maxPercent
        binding.loadExtractWaterProgressBar.max = viewModel.currentLocation.value!!.build!!.extractingWater!!.maxPercent

        with(binding) {
            loadAntiThiefProgressBar.max = viewModel.currentLocation.value!!.build!!.antiThief!!.maxPercent
            loadExtractWaterProgressBar.max = viewModel.currentLocation.value!!.build!!.extractingWater!!.maxPercent

            exitTextView.setOnClickListener {
                dismiss()
            }
            iconAntiThiefImageView.setOnClickListener {
                onLongClick(7)
            }
            iconResultAntiThiefForItemImageView.setOnClickListener {
                onLongClick(8)
            }
            iconResultWaterImageView.setOnClickListener {
                onLongClick(9)
            }
            destroyBuildButton.setOnClickListener {
                viewModel.setBuild(null)
            }
        }



        val dialog = AlertDialog.Builder(activity)

        loadAntiThief()
        startAntiThief()

        loadWater()
        startExtractWater()

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val window: Window = dialog!!.window!!

        with(window) {
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun startAntiThief() {
        val indicatorAntiThief = viewModel.currentLocation.value!!.build!!.antiThief
        antiThiefFinishBoolean = true
        antiThiefStartJob = lifecycleScope.launch {
            while (indicatorAntiThief!!.percent != indicatorAntiThief.maxPercent) {
                binding.loadAntiThiefProgressBar.progress = indicatorAntiThief.percent
                delay(1000L)
            }

            while (antiThiefFinishBoolean) {
                delay(1000L)
                binding.loadAntiThiefProgressBar.progress = indicatorAntiThief.percent
                antiThiefFinishBoolean = false
                antiThiefStartJob.cancel()
                loadAntiThief()
                startAntiThief()
            }
        }
    }

    private fun loadAntiThief() {
        val placeAntiThief = viewModel.getCurrentLocation().build!!.placeForAntiThief
        var antiThiefIndex = 7
        for(index in placeAntiThief.indices) {
            if (placeAntiThief[index] != null) {
                setImageIcon(antiThiefIndex, placeAntiThief[index]!!.item.imageId, placeAntiThief[index]!!.count)
            } else {
                setImageIcon(antiThiefIndex, viewModel.getIconForItemPutOnResImageMap(antiThiefIndex), 0)
            }
            antiThiefIndex++
        }
    }

    private fun startExtractWater() {
        val indicatorWaterExtract = viewModel.currentLocation.value!!.build!!.extractingWater
        waterFinishBoolean = true
        waterStartJob = lifecycleScope.launch {
            while (indicatorWaterExtract!!.percent != indicatorWaterExtract.maxPercent) {
                binding.loadExtractWaterProgressBar.progress = indicatorWaterExtract.percent
                delay(1000L)
            }

            while (waterFinishBoolean) {
                delay(1000L)
                binding.loadExtractWaterProgressBar.progress = indicatorWaterExtract.percent
                waterFinishBoolean = false
                waterStartJob.cancel()
                loadWater()
                startExtractWater()
            }
        }
    }
    private fun loadWater() {
        val placeWater = viewModel.getCurrentLocation().build!!.placeForWater
        val indexWater = 9
        if (viewModel.getCurrentLocation().build!!.placeForWater != null) {
            setImageIcon(indexWater, placeWater!!.item.imageId, placeWater.count)
        } else {
            setImageIcon(indexWater, viewModel.getIconForItemPutOnResImageMap(indexWater), 0)
        }
    }

    private fun setImageIcon(index: Int, imageItem: Int, countItem: Int) {
        when (index) {
            7 -> {
                binding.iconAntiThiefImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemAntiThiefTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemAntiThiefTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
            8 -> {
                binding.iconResultAntiThiefForItemImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemResultAntiThiefForItemTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemResultAntiThiefForItemTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
            9 -> {
                binding.iconResultWaterImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemResultWaterTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemResultWaterTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
        }

    }

    private fun onLongClick(index: Int): Boolean {
        val imageIconForBackpack: Int
        return when (index) {
            7 -> {
                imageIconForBackpack = viewModel.setAtiThief(7, null, viewModel.player.value!!.backpack)
                setImageIcon(7, imageIconForBackpack, 0)
                true
            }

            8 -> {
                imageIconForBackpack = viewModel.setAtiThief(8, null, viewModel.player.value!!.backpack)
                setImageIcon(8, imageIconForBackpack, 0)
                true
            }
            9 -> {
                imageIconForBackpack = viewModel.setExtractWater(9, viewModel.player.value!!.backpack)
                setImageIcon(9, imageIconForBackpack, 0)
                true
            }
            else -> {
                false
            }
        }
    }

}