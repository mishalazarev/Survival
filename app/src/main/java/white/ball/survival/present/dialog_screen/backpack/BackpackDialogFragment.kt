package white.ball.survival.present.dialog_screen.backpack

import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.INVISIBLE
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import white.ball.survival.R
import white.ball.survival.databinding.FragmentBackpackDialogBinding
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.extension_model.Level
import white.ball.survival.domain.repository.ItemUseRepository
import white.ball.survival.present.dialog_screen.backpack.adapter.BackpackAdapter
import white.ball.survival.present.dialog_screen.backpack.view_model.BackpackViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class BackpackDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentBackpackDialogBinding
    private lateinit var adapter: BackpackAdapter
    private lateinit var layoutManager: GridLayoutManager

    private val levelUp = Level()

    private lateinit var eatingSound: MediaPlayer
    private lateinit var drinkingSound: MediaPlayer

    private lateinit var createRecipeItem: MediaPlayer
    private lateinit var armorPutOnSound: MediaPlayer
    private lateinit var swordPutOnSound: MediaPlayer
    private lateinit var arrowPutOnSound: MediaPlayer
    private lateinit var scrollPutOnSound: MediaPlayer
    private lateinit var openShellSound: MediaPlayer

    private lateinit var buildAlreadyDoneSnackbar: Snackbar

    private val viewModel: BackpackViewModel by viewModelCreator {
        BackpackViewModel(
            it.uiAction,
            it.interactionWithEnvironment,
            it.playerAction
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.fragment_backpack_dialog, null)
        val dialog = AlertDialog.Builder(activity)
        layoutManager = GridLayoutManager(activity, CELL_BACKPACK_COLUMNS)
        binding = FragmentBackpackDialogBinding.bind(view)

        adapter = BackpackAdapter(object : ItemUseRepository {
            override fun setBuild(itemBuild: ItemsSlot?) {
                if (viewModel.getCurrentLocation().build == null) {
                    viewModel.setBuild(itemBuild)
                    createRecipeItem.start()
                    viewModel.player.value!!.backpack.forEach {
                        adapter.loadItemMenuChoice(adapter.menu, it)
                    }
                    adapter.notifyDataSetChanged()
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
                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun joinSimilarItems(itemsSlot: ItemsSlot) {
                viewModel.joinSimilarItemsSlot(itemsSlot)
                adapter.notifyDataSetChanged()
            }

            override fun openShell(itemShell: ItemsSlot) {
                viewModel.openShell(itemShell)
                openShellSound.start()
                adapter.notifyDataSetChanged()
            }

            override fun openScroll(itemScroll: ItemsSlot) {
                viewModel.openScroll(itemScroll)
                scrollPutOnSound.start()
                adapter.notifyDataSetChanged()
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

                val putOnImage = viewModel.setPutOn(index, itemsForPutOn)
                setImageIcon(index, putOnImage)

                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun setIntoCook(itemsForCook: ItemsSlot) {
                val index = when (itemsForCook.item.itemUse) {
                    ItemUse.FOR_COOK -> 4
                    ItemUse.FOR_FIRE -> 5
                    else -> throw IllegalArgumentException("Unknown number")
                }

                val putIntoCook = viewModel.setForCook(index, itemsForCook, viewModel.player.value!!.backpack)
                setImageIcon(index, putIntoCook)

                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun setAntiThief(itemPhosphorus: ItemsSlot) {
                viewModel.setAtiThief(7, itemPhosphorus, viewModel.player.value!!.backpack)

                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun growPlant(itemSeed: ItemsSlot) {
                viewModel.growPlant(itemSeed)
                adapter.notifyDataSetChanged()
            }

            override fun giveWaterPlant(itemWater: ItemsSlot) {
                viewModel.giveWaterForPlant(itemWater)
                adapter.notifyDataSetChanged()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun findOutInformation(items: ItemsSlot) {
                val findOutItemSnackbar = Snackbar.make(requireView(), "${requireContext().getString(items.item.nameItem)}", Snackbar.LENGTH_LONG)
                context?.let {
                    findOutItemSnackbar.setTextColor(it.getColor(R.color.gray)) }
                findOutItemSnackbar.setBackgroundTint(requireContext().getColor(R.color.head_background_dialog_fragment))
                findOutItemSnackbar.show()
            }

            override fun divideIntoQualityGroups(items: ItemsSlot) {
                viewModel.divideIntoQualityGroups(items)
                adapter.notifyDataSetChanged()
            }

            override fun threwItAway(items: ItemsSlot) {
                viewModel.threwItAway(items)
                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

            override fun threwAllItemsAway(items: ItemsSlot) {
                viewModel.threwAllItemsAway(items)
                isEmptyBackpack()
                adapter.notifyDataSetChanged()
            }

        },
            requireContext(),
            viewModel.currentLocation.value!!.build)

        with(binding) {
            backpackItemsRecyclerView.layoutManager = layoutManager
            backpackItemsRecyclerView.adapter = adapter
            adapter.mItemsSlotList = viewModel.player.value!!.backpack

            iconWeaponForItemImageView.setOnLongClickListener {
                swordPutOnSound.start()
                onLongClick(0)
            }
            iconArrowForItemImageView.setOnLongClickListener {
                onLongClick(1)
            }
            iconArmorForItemImageView.setOnLongClickListener {
                armorPutOnSound.start()
                onLongClick(2)
            }
            iconScrollForItemImageView.setOnClickListener {
                scrollPutOnSound.start()
                onLongClick(3)
            }

            exitTextView.setOnClickListener {
                dismiss()
            }

            addHealthImageButton.setOnClickListener { viewModel.upParameter('h') }
            addEnduranceImageButton.setOnClickListener { viewModel.upParameter('e') }
            addDamageImageButton.setOnClickListener { viewModel.upParameter('d') }
        }

        // load item in put on
        for (index in 0 until viewModel.player.value!!.placeForPutOn.size) {
            val imageForPutOn = if (viewModel.player.value!!.placeForPutOn[index] != null) {
                viewModel.player.value!!.placeForPutOn[index]!!.item.imageId
            } else {
                viewModel.getIconForItemPutOnResImageMap(index)
            }
            setImageIcon(index, imageForPutOn)
        }

        eatingSound = MediaPlayer.create(requireContext(), R.raw.eating)
        drinkingSound = MediaPlayer.create(requireContext(), R.raw.drinking)

        createRecipeItem = MediaPlayer.create(requireActivity().applicationContext, R.raw.create_recipe_item)
        armorPutOnSound = MediaPlayer.create(requireContext(), R.raw.armor_put_on)
        swordPutOnSound = MediaPlayer.create(requireContext(), R.raw.sword_put_on)
        arrowPutOnSound = MediaPlayer.create(requireContext(), R.raw.arrow_put_on)
        scrollPutOnSound = MediaPlayer.create(requireContext(), R.raw.scroll_put_on)
        openShellSound = MediaPlayer.create(requireContext(), R.raw.to_open_shell)

        return dialog
            .setView(view)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.player.observe(viewLifecycleOwner) {
            binding.namePlayerTextView.text = it.namePlayer

            binding.healthTextView.text = "${it.health.indicator.maxPercent}"
            binding.enduranceTextView.text = "${it.endurance.indicator.maxPercent}"
            binding.damageTextView.text = "${it.damage}"

            binding.indicatorHealthProgressBar.setValue(it.health.indicator.percent)
            binding.indicatorHealthProgressBar.setMaxValue(it.health.indicator.maxPercent)

            binding.indicatorEnduranceProgressBar.setValue(it.endurance.indicator.percent)
            binding.indicatorEnduranceProgressBar.setMaxValue(it.endurance.indicator.maxPercent)

            binding.indicatorExpProgressBar.setValue(it.exp)
            binding.indicatorExpProgressBar.setMaxValue(levelUp.getMaxExp(it.level))

            binding.experienceTextView.text = "${it.exp}"

            binding.levelTextView.text = "${levelUp.checkOnNewLevel(it.exp)}"
            binding.studyPointsTextView.text = "${it.studyPoints}"

            if (it.studyPoints > 0) {
                setEnableOrDisStudyPointsButton(true)
            } else {
                setEnableOrDisStudyPointsButton(false)
            }
        }

        isEmptyBackpack()


        return binding.root
    }


    private fun setEnableOrDisStudyPointsButton(flagEnabled: Boolean) {
        with(binding) {
            addHealthImageButton.isEnabled = flagEnabled
            addEnduranceImageButton.isEnabled = flagEnabled
            addDamageImageButton.isEnabled = flagEnabled
        }
    }

    override fun onStart() {
        super.onStart()
        buildAlreadyDoneSnackbar = Snackbar.make(binding.root, R.string.the_build_already_done, Snackbar.LENGTH_SHORT)
        with(buildAlreadyDoneSnackbar) {
            setTextColor(requireContext().getColor(R.color.gray))
            setBackgroundTint(requireContext().getColor(R.color.head_background_dialog_fragment))
        }

        viewModel.currentBackpack?.observe(viewLifecycleOwner) {
            adapter.mItemsSlotList = it
        }

        val window: Window = dialog!!.window!!
        with(window) {

            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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

    private fun onLongClick(index: Int): Boolean {
        val imageIconForBackpack: Int
        return when (index) {
            0 -> {
                imageIconForBackpack = viewModel.setPutOn(0, null)
                setImageIcon(0, imageIconForBackpack)
                true
            }

            1 -> {
                imageIconForBackpack = viewModel.setPutOn(1, null)
                setImageIcon(1, imageIconForBackpack)
                true
            }

            2 -> {
                imageIconForBackpack = viewModel.setPutOn(2, null)
                setImageIcon(2, imageIconForBackpack)
                true
            }

            3 -> {
                imageIconForBackpack = viewModel.setPutOn(3, null)
                setImageIcon(3, imageIconForBackpack)
                true
            }

            else -> {
                false
            }
        }
    }

    private fun isEmptyBackpack() {
        with(binding) {
            if (viewModel.player.value!!.backpack.isEmpty()) {
                wrapperBackpackLinearLayout.visibility = INVISIBLE
                backpackItemsRecyclerView.visibility = INVISIBLE
                captionEmptyBackpackTextView.visibility = VISIBLE
            } else {
                wrapperBackpackLinearLayout.visibility = VISIBLE
                backpackItemsRecyclerView.visibility = VISIBLE
                captionEmptyBackpackTextView.visibility = INVISIBLE
            }
        }
    }

    private fun setImageIcon(index: Int, imageItem: Int) {
        when (index) {
            // the icons are for put on
            0 -> binding.iconWeaponForItemImageView.setImageResource(imageItem)
            1 -> binding.iconArrowForItemImageView.setImageResource(imageItem)
            2 -> binding.iconArmorForItemImageView.setImageResource(imageItem)
            3 -> binding.iconScrollForItemImageView.setImageResource(imageItem)
        }

        isEmptyBackpack()
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        val CELL_BACKPACK_COLUMNS = 6
    }
}