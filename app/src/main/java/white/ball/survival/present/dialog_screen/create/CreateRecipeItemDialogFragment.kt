package white.ball.survival.present.dialog_screen.create

import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import white.ball.survival.R
import white.ball.survival.databinding.FragmentCreateRecipeItemDialogBinding
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Hut
import white.ball.survival.domain.model.build.StoneHouse
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.repository.RecipeItemCreateRepository
import white.ball.survival.present.dialog_screen.create.adapter.ArmorAdapter
import white.ball.survival.present.dialog_screen.create.adapter.BuildAdapter
import white.ball.survival.present.dialog_screen.create.adapter.WeaponAdapter
import white.ball.survival.present.dialog_screen.create.view_model.CreateViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator


class CreateRecipeItemDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentCreateRecipeItemDialogBinding
    private lateinit var weaponAdapter: WeaponAdapter
    private lateinit var armorAdapter: ArmorAdapter
    private lateinit var buildAdapter: BuildAdapter

    private lateinit var createRecipeItem: MediaPlayer

    private lateinit var itemCreatedSnackbar: Snackbar
    private lateinit var itemsNotEnoughSnackbar: Snackbar

    private val viewModel: CreateViewModel by viewModelCreator {
        CreateViewModel(
            it.playerAction
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val view = View.inflate(activity, R.layout.fragment_create_recipe_item_dialog, null)
            val dialog = AlertDialog.Builder(it)
            val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding = FragmentCreateRecipeItemDialogBinding.bind(view)

            context?.let { itContext -> {
                itemCreatedSnackbar.setTextColor(itContext.getColor(R.color.gray))
                itemCreatedSnackbar.setBackgroundTint(itContext.getColor(R.color.head_background_dialog_fragment))

                itemsNotEnoughSnackbar.setTextColor(itContext.getColor(R.color.gray))
                itemsNotEnoughSnackbar.setBackgroundTint(itContext.getColor(R.color.head_background_dialog_fragment))
                }
            }

            createRecipeItem = MediaPlayer.create(requireActivity().applicationContext, R.raw.create_recipe_item)

            weaponAdapter = WeaponAdapter(object : RecipeItemCreateRepository {

                override fun createItem(recipeItem: Slot) {
                    if (viewModel.createRecipeItem(recipeItem)) {
                        showTextCreateRecipeItem()
                        createRecipeItem.start()
                    } else {
                        showTextItemsIsNotEnough()
                    }
                    weaponAdapter.notifyDataSetChanged()
                }

            }, requireContext())

            armorAdapter = ArmorAdapter(object  : RecipeItemCreateRepository {
                override fun createItem(recipeItem: Slot) {
                    if (viewModel.createRecipeItem(recipeItem)) {
                        showTextCreateRecipeItem()
                        createRecipeItem.start()
                    } else {
                        showTextItemsIsNotEnough()
                    }
                    armorAdapter.notifyDataSetChanged()
                }

            }, requireContext())

            buildAdapter = BuildAdapter(object : RecipeItemCreateRepository {
                override fun createItem(recipeItem: Slot) {
                    if (viewModel.createRecipeItem(recipeItem)) {
                        showTextCreateRecipeItem()
                        createRecipeItem.start()
                    } else {
                        showTextItemsIsNotEnough()
                    }
                    buildAdapter.notifyDataSetChanged()
                }

            }, requireContext())

            weaponAdapter.weaponList = Weapon.values().toList()
            armorAdapter.armorList = Armor.values().toList()
            buildAdapter.buildList = arrayListOf(Well(), Bonfire(), Hut(), WoodHouse(), StoneHouse())

            with(binding) {
                recipeItemRecyclerview.layoutManager = linearLayoutManager

                weaponRecipeItemTextView.setOnClickListener {
                    if (!recipeItemRecyclerview.isVisible) {
                        recipeItemRecyclerview.adapter = weaponAdapter
                        isShowWorkshopSection(true)
                    }
                }

                armorRecipeItemTextView.setOnClickListener {
                    if (!recipeItemRecyclerview.isVisible) {
                        recipeItemRecyclerview.adapter = armorAdapter
                        isShowWorkshopSection(true)
                    }
                }

                buildRecipeItemTextView.setOnClickListener {
                    if (!recipeItemRecyclerview.isVisible) {
                        recipeItemRecyclerview.adapter = buildAdapter
                        isShowWorkshopSection(true)
                    }
                }


                iconBackImageButton.setOnClickListener {
                    isShowWorkshopSection(false)
                }

                exitTextView.setOnClickListener {
                    dismiss()
                }
            }

            dialog
                .setView(view)
                .create()
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        itemCreatedSnackbar = Snackbar.make(binding.root, R.string.notification_the_item_create, Snackbar.LENGTH_SHORT)
        itemsNotEnoughSnackbar = Snackbar.make(binding.root, R.string.notification_resources_is_not_enough, Snackbar.LENGTH_SHORT)
    }

    private fun showTextCreateRecipeItem() {
        itemCreatedSnackbar.show()
    }

    private fun showTextItemsIsNotEnough() {
        itemsNotEnoughSnackbar.show()
    }

    private fun isShowWorkshopSection(isOpenShowWorkShopBoolean: Boolean) {
        with(binding) {
            if (isOpenShowWorkShopBoolean) {
                recipeItemRecyclerview.isVisible = true
                iconBackImageButton.isVisible = true
                weaponRecipeItemTextView.isVisible = false
                armorRecipeItemTextView.isVisible = false
                buildRecipeItemTextView.isVisible = false
            } else {
                recipeItemRecyclerview.isVisible = false
                iconBackImageButton.isVisible = false
                weaponRecipeItemTextView.isVisible = true
                armorRecipeItemTextView.isVisible = true
                buildRecipeItemTextView.isVisible = true
            }
        }
    }

}