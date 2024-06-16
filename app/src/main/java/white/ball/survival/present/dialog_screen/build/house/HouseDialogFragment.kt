package white.ball.survival.present.dialog_screen.build.house

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import white.ball.survival.R
import white.ball.survival.databinding.FragmentHouseDialogBinding
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.PlantAbility
import white.ball.survival.domain.repository.ItemUseRepository
import white.ball.survival.present.dialog_screen.backpack.BackpackDialogFragment
import white.ball.survival.present.dialog_screen.backpack.adapter.BackpackAdapter
import white.ball.survival.present.dialog_screen.backpack.view_model.BackpackViewModel
import white.ball.survival.present.dialog_screen.build.bonfire.BonfireDialogFragment
import white.ball.survival.present.screen.main_screen.game_play.fight.FightFragment.Companion.ONE_SECOND
import white.ball.survival.present.view_model_factory.viewModelCreator

class HouseDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentHouseDialogBinding

    private lateinit var cookingStartJob: Job
    private var cookingFinishBoolean = true

    private lateinit var antiThiefStartJob: Job
    private var antiThiefFinishBoolean = true

    private lateinit var waterExtractingStartJob: Job
    private var waterFinishBoolean = true

    private lateinit var growingPlantStartJob: Job
    private var growingPlantFinishBoolean = true

    private lateinit var adapterBackpack: BackpackAdapter
    private lateinit var layoutManagerCells: GridLayoutManager

    private lateinit var buildAlreadyDoneSnackbar: Snackbar
    private lateinit var growPlantSnackbar: Snackbar
    private lateinit var giveWaterPlantSnackbar: Snackbar
    private lateinit var notNeedGiveWaterPlantSnackbar: Snackbar
    private lateinit var aboutPlantSnackbar: Snackbar

    private lateinit var gardenMenu: PopupMenu

    private val viewModel: BackpackViewModel by viewModelCreator {
        BackpackViewModel(
            it.uiAction,
            it.interactionWithEnvironment,
            it.playerAction
        )
    }

    private lateinit var eatingSound: MediaPlayer
    private lateinit var drinkingSound: MediaPlayer

    private lateinit var createRecipeItem: MediaPlayer
    private lateinit var armorPutOnSound: MediaPlayer
    private lateinit var swordPutOnSound: MediaPlayer
    private lateinit var arrowPutOnSound: MediaPlayer
    private lateinit var scrollPutOnSound: MediaPlayer
    private lateinit var openShellSound: MediaPlayer

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_house_dialog, null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentHouseDialogBinding.bind(view)
        layoutManagerCells = GridLayoutManager(activity, BackpackDialogFragment.CELL_BACKPACK_COLUMNS)

        eatingSound = MediaPlayer.create(requireContext(), R.raw.eating)
        drinkingSound = MediaPlayer.create(requireContext(), R.raw.drinking)

        createRecipeItem = MediaPlayer.create(requireActivity().applicationContext, R.raw.create_recipe_item)
        armorPutOnSound = MediaPlayer.create(requireContext(), R.raw.armor_put_on)
        swordPutOnSound = MediaPlayer.create(requireContext(), R.raw.sword_put_on)
        arrowPutOnSound = MediaPlayer.create(requireContext(), R.raw.arrow_put_on)
        scrollPutOnSound = MediaPlayer.create(requireContext(), R.raw.scroll_put_on)
        openShellSound = MediaPlayer.create(requireContext(), R.raw.to_open_shell)

        with(binding) {
            loadCookFoodProgressBar.max = viewModel.currentLocation.value!!.build!!.cooking!!.maxPercent
            loadExtractWaterProgressBar.max = viewModel.currentLocation.value!!.build!!.extractingWater!!.maxPercent
            loadAntiThiefProgressBar.max = viewModel.currentLocation.value!!.build!!.antiThief!!.maxPercent
            loadIndicatorGrownPlant()

            exitTextView.setOnClickListener {
                dismiss()
            }

            iconCookFoodForItemImageView.setOnClickListener {
                onLongClick(4)
            }

            iconWoodForItemImageView.setOnClickListener {
                onLongClick(5)
            }

            iconResultCookForItemImageView.setOnClickListener {
                onLongClick(6)
            }

            iconAntiThiefImageView.setOnClickListener {
                onLongClick(7)
            }

            iconResultAntiThiefForItemImageView.setOnClickListener {
                onLongClick(8)
            }

            iconResultWaterImageView.setOnClickListener {
                onLongClick(9)
                adapterBackpack.notifyDataSetChanged()
            }

            iconResultFruitImageView.setOnClickListener {
                onLongClick(10)
                adapterBackpack.notifyDataSetChanged()
            }

            wrapperGarden.setOnClickListener {
                if (viewModel.currentLocation.value!!.build!!.plant != null) {
                    gardenMenu.show()
                }
            }

            destroyBuildButton.setOnClickListener {
                viewModel.setBuild(null)
            }
        }

        gardenMenu = PopupMenu(requireContext(), binding.wrapperGarden)
        gardenMenu.inflate(R.menu.menu_garden_use)

        gardenMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.give_water_plant -> {
                    var isHaveWaterInBackpackBoolean = true
                    val backpackPlayer = viewModel.player.value!!.backpack
                    for (index in backpackPlayer.indices) {
                        if (backpackPlayer[index].item.itemUse == ItemUse.DRINK) {
                            isHaveWaterInBackpackBoolean = false
                            viewModel.giveWaterForPlant(backpackPlayer[index])
                            adapterBackpack.notifyDataSetChanged()
                            break
                        }
                    }
                    if (isHaveWaterInBackpackBoolean) giveWaterPlantSnackbar.show()
                    true
                }
                R.id.cut_down_plant -> {
                    if (viewModel.currentLocation.value!!.build!!.plant != null) {
                        viewModel.destroyPlant()
                        loadGarden()
                    }
                    true
                }
                R.id.about_plant -> {
                    aboutPlantSnackbar = Snackbar.make(
                        binding.root,
                        getText(viewModel.currentLocation.value!!.build!!.plant!!.namePlant),
                        Snackbar.LENGTH_SHORT
                    )
                    with(aboutPlantSnackbar) {
                        setTextColor(requireContext().getColor(R.color.gray))
                        setBackgroundTint(requireContext().getColor(R.color.green))
                    }

                    if (!aboutPlantSnackbar.isShown) {
                        aboutPlantSnackbar.show()
                    }
                    true
                }
                 else -> false
            }
        }

        adapterBackpack = BackpackAdapter(object : ItemUseRepository {
            override fun setBuild(itemBuild: ItemsSlot?) {
                if (viewModel.getCurrentLocation().build == null) {
                    viewModel.setBuild(itemBuild)
                    createRecipeItem.start()
                    viewModel.player.value!!.backpack.forEach {
                        adapterBackpack.loadItemMenuChoice(adapterBackpack.menu, it)
                    }
                    adapterBackpack.notifyDataSetChanged()
                } else if (!buildAlreadyDoneSnackbar.isShown) {
                    buildAlreadyDoneSnackbar.show()
                }
            }

            override fun takeFood(foods: ItemsSlot) {
                if (foods.item.itemUse == ItemUse.FOOD || foods.item.itemUse == ItemUse.FOR_COOK) {
                    eatingSound.start()
                } else if (foods.item.itemUse == ItemUse.DRINK) {
                    drinkingSound.start()
                }
                viewModel.takeFood(foods)
                adapterBackpack.notifyDataSetChanged()
            }

            override fun joinSimilarItems(itemsSlot: ItemsSlot) {
                viewModel.joinSimilarItemsSlot(itemsSlot)
                adapterBackpack.notifyDataSetChanged()
            }

            override fun openShell(itemShell: ItemsSlot) {
                viewModel.openShell(itemShell)
                openShellSound.start()
                adapterBackpack.notifyDataSetChanged()
            }

            override fun openScroll(itemScroll: ItemsSlot) {
                viewModel.openScroll(itemScroll)
                scrollPutOnSound.start()
                adapterBackpack.notifyDataSetChanged()
            }

            override fun setPutOnItem(itemsForPutOn: ItemsSlot) {

                val index: Int = when (itemsForPutOn.item.itemUse) {
                    ItemUse.WEAPON -> {
                        swordPutOnSound.start()
                        0
                    }
                    ItemUse.BOW -> {
                        arrowPutOnSound.start()
                        0
                    }
                    ItemUse.ARROW -> {
                        1
                    }
                    ItemUse.ARMOR -> {
                        armorPutOnSound.start()
                        2
                    }
                    ItemUse.OPENED_SCROLL -> {
                        scrollPutOnSound.start()
                        3
                    }
                    else -> throw IllegalArgumentException("Unknown number")
                }

                val correctedIndex = if (ItemUse.ARROW == itemsForPutOn.item.itemUse) {
                    viewModel.player.value!!.placeForPutOn[1]!!.count
                } else {
                    0
                }
                val putOnImage = viewModel.setPutOn(index, itemsForPutOn)
                setImageIcon(index, putOnImage, correctedIndex)

                adapterBackpack.notifyDataSetChanged()
            }

            override fun setIntoCook(itemsForCook: ItemsSlot) {
                val index = when (itemsForCook.item.itemUse) {
                    ItemUse.FOR_COOK -> 4
                    ItemUse.FOR_FIRE -> 5
                    else -> throw IllegalArgumentException("Unknown number")
                }

                val putIntoCook = viewModel.setForCook(index, itemsForCook, viewModel.player.value!!.backpack)
                val correctedIndex = hashMapOf(4 to 0, 5 to 1)

                setImageIcon(index, putIntoCook, viewModel.currentLocation.value!!.build!!.placeForCook!![correctedIndex[index]!!]!!.count)

                adapterBackpack.notifyDataSetChanged()
            }

            override fun setAntiThief(itemPhosphorus: ItemsSlot) {
                val putAntiThief = viewModel.setAtiThief(7, itemPhosphorus, viewModel.player.value!!.backpack)
                setImageIcon(7, putAntiThief, viewModel.currentLocation.value!!.build!!.placeForAntiThief[0]!!.count)

                adapterBackpack.notifyDataSetChanged()
            }

            override fun growPlant(itemSeed: ItemsSlot) {
                if (viewModel.currentLocation.value!!.build!!.plant == null) {
                    viewModel.growPlant(itemSeed)
                    loadIndicatorGrownPlant()
                    adapterBackpack.notifyDataSetChanged()
                } else if (!growPlantSnackbar.isShown) {
                    growPlantSnackbar.show()
                }
            }

            override fun giveWaterPlant(itemWater: ItemsSlot) {
                val plant = viewModel.currentLocation.value!!.build!!.plant!!
                if (plant.ability == PlantAbility.ADULT && plant.currentLevel == plant.commonProcess.size - 1 && !notNeedGiveWaterPlantSnackbar.isShown) {
                    notNeedGiveWaterPlantSnackbar.show()
                } else {
                    viewModel.giveWaterForPlant(itemWater)
                    adapterBackpack.notifyDataSetChanged()
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun findOutInformation(items: ItemsSlot) {
                val findOutItemSnackbar = Snackbar.make(requireView(), "${requireContext().getString(items.item.nameItem)} - ${requireContext().getString(items.item.descrItem)}", Snackbar.LENGTH_LONG)
                context?.let { findOutItemSnackbar.setTextColor(it.getColor(R.color.gray)) }
                findOutItemSnackbar.setBackgroundTint(requireContext().getColor(R.color.head_background_dialog_fragment))
                findOutItemSnackbar.show()
            }

            override fun divideIntoQualityGroups(items: ItemsSlot) {
                viewModel.divideIntoQualityGroups(items)
                adapterBackpack.notifyDataSetChanged()
            }

            override fun threwItAway(items: ItemsSlot) {
                viewModel.threwItAway(items)
                adapterBackpack.notifyDataSetChanged()
            }

            override fun threwAllItemsAway(items: ItemsSlot) {
                viewModel.threwAllItemsAway(items)
                adapterBackpack.notifyDataSetChanged()
            }

        },
            requireContext(),
            viewModel.currentLocation.value!!.build!!)

        binding.backpackItemsRecyclerView.layoutManager = layoutManagerCells
        binding.backpackItemsRecyclerView.adapter = adapterBackpack
        adapterBackpack.mItemsSlotList = viewModel.player.value!!.backpack

        loadCook()
        loadAntiThief()

        startAntiThief()
        startCooking()

        loadWater()
        startExtractWater()

        loadGarden()
        startGrowingPlant()

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!

        buildAlreadyDoneSnackbar = Snackbar.make(binding.root, R.string.the_build_already_done, Snackbar.LENGTH_SHORT)
        with(buildAlreadyDoneSnackbar) {
            setTextColor(requireContext().getColor(R.color.gray))
            setBackgroundTint(requireContext().getColor(R.color.head_background_dialog_fragment))
        }

        growPlantSnackbar = Snackbar.make(binding.root, R.string.the_plant_alreay_done, Snackbar.LENGTH_SHORT)
        with(growPlantSnackbar) {
            setTextColor(requireContext().getColor(R.color.gray))
            setBackgroundTint(requireContext().getColor(R.color.green))
        }

        giveWaterPlantSnackbar = Snackbar.make(binding.root, R.string.backpack_has_not_item_water, Snackbar.LENGTH_SHORT)
        with(giveWaterPlantSnackbar) {
            setTextColor(requireContext().getColor(R.color.gray))
            setBackgroundTint(requireContext().getColor(android.R.color.holo_blue_dark))
        }

        notNeedGiveWaterPlantSnackbar = Snackbar.make(binding.root, R.string.not_need_give_water_plant, Snackbar.LENGTH_SHORT)
        with(notNeedGiveWaterPlantSnackbar) {
            setTextColor(requireContext().getColor(R.color.gray))
            setBackgroundTint(requireContext().getColor(android.R.color.holo_red_dark))
        }

        with(window) {
            setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onStop() {
        super.onStop()
        eatingSound.stop()
        drinkingSound.stop()
        createRecipeItem.stop()
        armorPutOnSound.stop()
        swordPutOnSound.stop()
        arrowPutOnSound.stop()
        scrollPutOnSound.stop()
        openShellSound.stop()

        eatingSound.release()
        drinkingSound.release()
        createRecipeItem.release()
        armorPutOnSound.release()
        swordPutOnSound.release()
        arrowPutOnSound.release()
        scrollPutOnSound.release()
        openShellSound.release()
    }

    private fun startCooking() {
        cookingFinishBoolean = true
        cookingStartJob = lifecycleScope.launch {
            while (viewModel.currentLocation.value!!.build!!.cooking!!.percent != viewModel.currentLocation.value!!.build!!.cooking!!.maxPercent) {
                binding.loadCookFoodProgressBar.progress = viewModel.currentLocation.value!!.build!!.cooking!!.percent
                delay(ONE_SECOND)
            }

            while (cookingFinishBoolean) {
                delay(ONE_SECOND)
                binding.loadCookFoodProgressBar.progress = viewModel.currentLocation.value!!.build!!.cooking!!.percent
                cookingFinishBoolean = false
                cookingStartJob.cancel()
                loadCook()
                startCooking()
            }
        }
    }

    private fun loadCook() {
        val placeForCook = viewModel.getCurrentLocation().build!!.placeForCook!!
        var cookIndex = 4
        for (index in placeForCook.indices) {
            if (placeForCook[index] != null) {
                setImageIcon(cookIndex, placeForCook[index]!!.item.imageId, placeForCook[index]!!.count)
            } else {
                setImageIcon(cookIndex, viewModel.getIconForItemPutOnResImageMap(cookIndex), 0)
            }
            cookIndex++
        }
    }
    private fun startAntiThief() {
        val indicatorAntiThief = viewModel.currentLocation.value!!.build!!.antiThief
        antiThiefFinishBoolean = true
        antiThiefStartJob = lifecycleScope.launch {
            while (indicatorAntiThief!!.percent != indicatorAntiThief.maxPercent) {
                binding.loadAntiThiefProgressBar.progress = indicatorAntiThief.percent
                delay(ONE_SECOND)
            }

            while (antiThiefFinishBoolean) {
                delay(ONE_SECOND)
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
        waterExtractingStartJob = lifecycleScope.launch {
            while (indicatorWaterExtract!!.percent != indicatorWaterExtract.maxPercent) {
                binding.loadExtractWaterProgressBar.progress = indicatorWaterExtract.percent
                delay(ONE_SECOND)
            }

            while (waterFinishBoolean) {
                delay(ONE_SECOND)
                binding.loadExtractWaterProgressBar.progress = indicatorWaterExtract.percent
                waterFinishBoolean = false
                waterExtractingStartJob.cancel()
                loadWater()
                startExtractWater()
            }
        }
    }
    private fun loadWater() {
        val placeWater = viewModel.getCurrentLocation().build!!.placeForWater
        val indexWater = 9
        if (viewModel.getCurrentLocation().build!!.placeForWater != null) {
            binding.loadGrowPlantProgressBar.max = viewModel.currentLocation.value!!.build!!.plant!!.indicatorGrow.maxPercent
            setImageIcon(indexWater, placeWater!!.item.imageId, placeWater!!.count)
        } else {
            setImageIcon(indexWater, viewModel.getIconForItemPutOnResImageMap(indexWater), 0)
        }
    }

    private fun startGrowingPlant() {
        val indicatorGrowingPlant = viewModel.currentLocation.value!!.build!!.plant?.indicatorGrow
        growingPlantFinishBoolean = true
        growingPlantStartJob = lifecycleScope.launch {
            while (indicatorGrowingPlant?.percent != indicatorGrowingPlant?.maxPercent) {
                binding.loadGrowPlantProgressBar.progress = indicatorGrowingPlant?.percent!!
                delay(ONE_SECOND)
            }

            while (growingPlantFinishBoolean) {
                delay(ONE_SECOND)
                indicatorGrowingPlant?.let {
                    binding.loadGrowPlantProgressBar.progress = it.percent
                }
                growingPlantFinishBoolean = false
                growingPlantStartJob.cancel()
                loadGarden()
                startGrowingPlant()
            }
        }
    }

    private fun loadGarden() {
        val placeForGarden = viewModel.getCurrentLocation().build!!.placeForFruit

        var growPlantIndex = 10
        if (placeForGarden != null) {
            setImageIcon(growPlantIndex, placeForGarden.item.imageId, placeForGarden.count)
        } else {
            setImageIcon(growPlantIndex, viewModel.getIconForItemPutOnResImageMap(growPlantIndex), 0)
        }
        growPlantIndex++

        if (viewModel.currentLocation.value!!.build?.plant?.currentLevel != null) {
            binding.iconLocationImageView.setImageResource(viewModel.currentLocation.value!!.build!!.plant!!.commonProcess[viewModel.currentLocation.value!!.build!!.plant!!.currentLevel])
        } else {
            binding.iconLocationImageView.setImageResource(0)
        }
    }

    private fun loadIndicatorGrownPlant() {
        viewModel.currentLocation.value!!.build?.plant?.indicatorGrow?.let {
            binding.loadGrowPlantProgressBar.max = it.maxPercent
        }
    }

    private fun setImageIcon(index: Int, imageItem: Int, countItem: Int) {
        when (index) {
            4 -> {
                binding.iconCookFoodForItemImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemCookFoodForItemTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemCookFoodForItemTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
            5 -> {
                binding.iconWoodForItemImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemWoodForItemTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemWoodForItemTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
            6 -> {
                binding.iconResultCookForItemImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemResultCookForItemTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemResultCookForItemTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
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
            10 -> {
                binding.iconResultFruitImageView.setImageResource(imageItem)
                if (countItem == 0) {
                    binding.countItemResultFruitTextView.setText(R.string.empty_text)
                } else {
                    binding.countItemResultFruitTextView.text = "$countItem ${BonfireDialogFragment.COUNT_ITEM_TEXT}"
                }
            }
        }
        adapterBackpack.notifyDataSetChanged()
    }

    private fun onLongClick(index: Int): Boolean {
        val imageIconForBackpack: Int
        return when (index) {
            4 -> {
                imageIconForBackpack = viewModel.setForCook(4, null, viewModel.player.value!!.backpack)
                setImageIcon(4, imageIconForBackpack, 0)
                true
            }
            5 -> {
                imageIconForBackpack = viewModel.setForCook(5, null, viewModel.player.value!!.backpack)
                setImageIcon(5, imageIconForBackpack, 0)
                true
            }
            6 -> {
                imageIconForBackpack = viewModel.setForCook(6, null, viewModel.player.value!!.backpack)
                setImageIcon(6, imageIconForBackpack, 0)
                true
            }
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
            10 -> {
                imageIconForBackpack = viewModel.setPlantFruit(10)
                setImageIcon(10, imageIconForBackpack, 0)
                true
            }
            else -> {
                false
            }
        }
    }
}