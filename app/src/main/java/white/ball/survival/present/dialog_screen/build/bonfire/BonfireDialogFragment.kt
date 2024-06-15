package white.ball.survival.present.dialog_screen.build.bonfire

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentBonfireDialogBinding
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.ItemUseRepository
import white.ball.survival.present.dialog_screen.backpack.adapter.BackpackAdapter
import white.ball.survival.present.dialog_screen.backpack.view_model.BackpackViewModel
import white.ball.survival.present.screen.main_screen.game_play.fight.FightFragment.Companion.TAG
import white.ball.survival.present.view_model_factory.viewModelCreator

class BonfireDialogFragment() : DialogFragment() {

    private lateinit var binding: FragmentBonfireDialogBinding
    private lateinit var adapter: BackpackAdapter

    private lateinit var cookingStartJob: Job
    private var cookingFinishBoolean = true

    private lateinit var antiThiefStartJob: Job
    private var antiThiefFinishBoolean = true

    private val viewModel: BackpackViewModel by viewModelCreator {
        BackpackViewModel(
            it.uiAction,
            it.interactionWithEnvironment,
            it.playerAction
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_bonfire_dialog, null)
        val layoutManager = GridLayoutManager(activity, CELL_BONFIRE_COLUMNS)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentBonfireDialogBinding.bind(view)
        binding.loadCookFoodProgressBar.max = viewModel.currentLocation.value!!.build!!.cooking!!.maxPercent
        binding.loadAntiThiefProgressBar.max = viewModel.currentLocation.value!!.build!!.antiThief!!.maxPercent

        adapter = BackpackAdapter(object : ItemUseRepository {
            override fun setBuild(itemBuild: ItemsSlot?) {
                // no code
            }

            override fun takeFood(food: ItemsSlot) {
                viewModel.takeFood(food)
                loadBackpack()
            }

            override fun joinSimilarItems(itemsSlot: ItemsSlot) {
                viewModel.joinSimilarItemsSlot(itemsSlot)
                loadBackpack()
            }

            override fun openShell(itemShell: ItemsSlot) {
                // no code
            }

            override fun openScroll(itemScroll: ItemsSlot) {}

            override fun setPutOnItem(itemsForPutOn: ItemsSlot) {}

            override fun setIntoCook(itemsForCook: ItemsSlot) {
                val index = when (itemsForCook.item.itemUse) {
                    ItemUse.FOR_COOK -> 4
                    ItemUse.FOR_FIRE -> 5
                    else -> throw IllegalArgumentException("Unknown number")
                }

                val putIntoCook = viewModel.setForCook(index, itemsForCook, viewModel.player.value!!.backpack)
                setImageIcon(index, putIntoCook)
                loadBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun setAntiThief(itemPhosphorus: ItemsSlot) {
                val putAntiThief = viewModel.setAtiThief(7, itemPhosphorus, viewModel.player.value!!.backpack)

                setImageIcon(7, putAntiThief)
                loadBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun growPlant(itemSeed: ItemsSlot) {
                viewModel.growPlant(itemSeed)
            }

            override fun giveWaterPlant(itemWater: ItemsSlot) {
                viewModel.giveWaterForPlant(itemWater)
                adapter.notifyDataSetChanged()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun findOutInformation(items: ItemsSlot) {
                val findOutItemSnackbar = Snackbar.make(requireView(), "${requireContext().getString(items.item.nameItem)}", Snackbar.LENGTH_LONG)
                context?.let {
                    findOutItemSnackbar.setBackgroundTint(it.getColor(R.color.head_background_dialog_fragment))
                    findOutItemSnackbar.setTextColor(it.getColor(R.color.gray))
                }

                findOutItemSnackbar.show()
            }

            override fun divideIntoQualityGroups(items: ItemsSlot) {
                viewModel.divideIntoQualityGroups(items)
                adapter.notifyDataSetChanged()
            }

            override fun threwItAway(items: ItemsSlot) {
                viewModel.threwItAway(items)
                loadBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun threwAllItemsAway(items: ItemsSlot) {
                viewModel.threwAllItemsAway(items)
                loadBackpack()
                adapter.notifyDataSetChanged()
            }
        }, requireContext(), viewModel.currentLocation.value!!.build!!)

        with(binding) {
            bonfireItemsRecyclerView.layoutManager = layoutManager
            bonfireItemsRecyclerView.adapter = adapter

            exitTextView.setOnClickListener {
                dismiss()
            }
            iconCookFoodForItemImageView.setOnClickListener {
                onLongClick(4)
                loadBackpack()
            }
            iconWoodForItemImageView.setOnClickListener {
                onLongClick(5)
                loadBackpack()
            }
            iconResultCookForItemImageView.setOnClickListener {
                onLongClick(6)
                loadBackpack()
            }
            iconAntiThiefImageView.setOnClickListener {
                onLongClick(7)
                loadBackpack()
            }
            iconResultAntiThiefForItemImageView.setOnClickListener {
                onLongClick(8)
                loadBackpack()
            }
            destroyBuildButton.setOnClickListener {
                viewModel.setBuild(null)
                dismiss()
            }
        }

        loadCook()
        loadAntiThief()

        loadBackpack()
        startAntiThief()
        startCooking()

        return dialog
            .setView(view)
            .create()
    }

    private fun startAntiThief() {
        antiThiefFinishBoolean = true
        antiThiefStartJob = lifecycleScope.launch {
            while (viewModel.currentLocation.value!!.build!!.antiThief!!.percent != viewModel.currentLocation.value!!.build!!.antiThief!!.maxPercent) {
                binding.loadAntiThiefProgressBar.progress =
                    viewModel.currentLocation.value!!.build!!.antiThief!!.percent
                delay(1000L)
            }

            while (antiThiefFinishBoolean) {
                delay(1000L)
                binding.loadAntiThiefProgressBar.progress = viewModel.currentLocation.value!!.build!!.antiThief!!.percent
                antiThiefFinishBoolean = false
                antiThiefStartJob.cancel()
                loadAntiThief()
                startAntiThief()
            }
        }
    }


    private fun loadAntiThief() {
        var antiThiefIndex = 7
        for(index in viewModel.getCurrentLocation().build!!.placeForAntiThief.indices) {
            if (viewModel.getCurrentLocation().build!!.placeForAntiThief[index] != null) {
                setImageIcon(antiThiefIndex, viewModel.getCurrentLocation().build!!.placeForAntiThief[index]!!.item.imageId)
            } else {
                setImageIcon(antiThiefIndex, viewModel.getIconForItemPutOnResImageMap(antiThiefIndex))
            }
            antiThiefIndex++
        }
    }

    private fun startCooking() {
        cookingFinishBoolean = true
        cookingStartJob = lifecycleScope.launch {
            while (viewModel.currentLocation.value!!.build!!.cooking!!.percent != viewModel.currentLocation.value!!.build!!.cooking!!.maxPercent) {
                binding.loadCookFoodProgressBar.progress = viewModel.currentLocation.value!!.build!!.cooking!!.percent
                delay(1000L)
            }

            while (cookingFinishBoolean) {
                delay(1000L)
                binding.loadCookFoodProgressBar.progress = viewModel.currentLocation.value!!.build!!.cooking!!.percent
                cookingFinishBoolean = false
                cookingStartJob.cancel()
                loadCook()
                startCooking()
            }
        }
    }

    private fun loadCook() {
        var cookIndex = 4
        for (index in viewModel.getCurrentLocation().build!!.placeForCook!!.indices) {
            if (viewModel.getCurrentLocation().build!!.placeForCook!![index] != null) {
                setImageIcon(cookIndex, viewModel.getCurrentLocation().build!!.placeForCook!![index]!!.item.imageId)
            } else {
                setImageIcon(cookIndex, viewModel.getIconForItemPutOnResImageMap(cookIndex))
            }
            cookIndex++
        }
    }

    private fun loadBackpack() {
        val sortItemsList = mutableListOf<ItemsSlot>()
        viewModel.player.value!!.backpack.forEach {
            if (it.item.itemUse == ItemUse.FOR_FIRE || it.item.itemUse == ItemUse.FOR_COOK || it.item.itemUse == ItemUse.ANTI_THIEF) {
                sortItemsList.add(it)
            }
            Log.i(TAG, "$sortItemsList")
        }
        adapter.mItemsSlotList = sortItemsList
    }

    override fun onStart() {
        super.onStart()
        val window: Window = dialog!!.window!!

        with(window) {
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }


    private fun setImageIcon(index: Int, imageItem: Int) {
        when (index) {
            4 -> binding.iconCookFoodForItemImageView.setImageResource(imageItem)
            5 -> binding.iconWoodForItemImageView.setImageResource(imageItem)
            6 -> binding.iconResultCookForItemImageView.setImageResource(imageItem)
            7 -> binding.iconAntiThiefImageView.setImageResource(imageItem)
            8 -> binding.iconResultAntiThiefForItemImageView.setImageResource(imageItem)
        }
    }

    private fun onLongClick(index: Int): Boolean {
        val imageIconForBackpack: Int
        return when (index) {
            4 -> {
                imageIconForBackpack = viewModel.setForCook(4, null, viewModel.player.value!!.backpack)
                setImageIcon(4, imageIconForBackpack)
                true
            }

            5 -> {
                imageIconForBackpack = viewModel.setForCook(5, null, viewModel.player.value!!.backpack)
                setImageIcon(5, imageIconForBackpack)
                true
            }

            6 -> {
                imageIconForBackpack = viewModel.setForCook(6, null, viewModel.player.value!!.backpack)
                setImageIcon(6, imageIconForBackpack)
                true
            }

            7 -> {
                imageIconForBackpack = viewModel.setAtiThief(7, null, viewModel.player.value!!.backpack)
                setImageIcon(7, imageIconForBackpack)
                true
            }

            8 -> {
                imageIconForBackpack = viewModel.setAtiThief(8, null, viewModel.player.value!!.backpack)
                setImageIcon(8, imageIconForBackpack)
                true
            }
            else -> {
                false
            }
        }
    }

    companion object {
        @JvmStatic
        val CELL_BONFIRE_COLUMNS = 8
    }


}