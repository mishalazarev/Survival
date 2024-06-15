package white.ball.survival.present.dialog_screen.create.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.RecipeItemBinding
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.repository.RecipeItemCreateRepository

class WeaponAdapter(
    private val recipeItemCreateRepository: RecipeItemCreateRepository,
    private val context: Context
) : RecyclerView.Adapter<WeaponAdapter.WeaponHolder>(), View.OnClickListener {

    var weaponList: List<Weapon> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class WeaponHolder(val binding: RecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipeItem: Weapon) {
            with(binding) {
                createRecipeItemButton.tag = recipeItem
                nameRecipeItemTextView.setText(recipeItem.nameItem)
            }

            if (binding.wrapperRecipeItemLinearLayout.isNotEmpty()) binding.wrapperRecipeItemLinearLayout.removeAllViews()

            for (index in recipeItem.recipe.indices) {
                val itemSlotForCreateTextView = TextView(context)
                itemSlotForCreateTextView.setBackgroundResource(recipeItem.recipe[index].item.imageId)
                itemSlotForCreateTextView.text = recipeItem.recipe[index].count.toString()
                itemSlotForCreateTextView.gravity = Gravity.BOTTOM
                itemSlotForCreateTextView.setTextColor(Color.BLACK)
                binding.wrapperRecipeItemLinearLayout.addView(itemSlotForCreateTextView)
            }

            with(binding) {
                if (recipeItem.itemUse == ItemUse.ARROW) {
                    recipeItemTextView.text = "+4"
                    recipeItemTextView.setBackgroundResource(recipeItem.imageId)
                } else {
                    recipeItemTextView.text = "+1"
                    recipeItemTextView.setBackgroundResource(recipeItem.imageId)
                }

                mainTextView.text = recipeItem.damage.toString()
                secondTextTextView.setText(recipeItem.itemEffect.nameText)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemBinding.inflate(layoutInflater, parent, false)

        binding.createRecipeItemButton.setOnClickListener(this)

        return WeaponHolder(binding)
    }

    override fun getItemCount(): Int = weaponList.size

    override fun onBindViewHolder(holder: WeaponHolder, position: Int) {
        val recipeWeapon = weaponList[position]

        holder.bind(recipeWeapon)
    }

    override fun onClick(view: View) {
        val recipeItem = view.tag as Weapon

        recipeItemCreateRepository.createItem(recipeItem)
    }

}