package white.ball.survival.present.dialog_screen.create.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.R
import white.ball.survival.databinding.RecipeItemBinding
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.repository.RecipeItemCreateRepository

class ArmorAdapter(
    private val recipeItemCreateRepository: RecipeItemCreateRepository,
    private val context: Context
): RecyclerView.Adapter<ArmorAdapter.ArmorHolder>(), View.OnClickListener {

    var armorList: List<Armor> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ArmorHolder(val binding: RecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipeItem: Armor) {
            with(binding) {
                captionMainTextView.text = context.getText(R.string.caption_impact_protection)
                captionSecondTextView.text = context.getText(R.string.resisting)

                recipeItemTextView.text = "+1"
                recipeItemTextView.setBackgroundResource(recipeItem.imageId)
                createRecipeItemButton.tag = recipeItem
                nameRecipeItemTextView.setText(recipeItem.nameItem)
            }

            if (binding.wrapperRecipeItemLinearLayout.isEmpty()) {
                for (index in recipeItem.recipe.indices) {
                    val itemSlotForCreateTextView = TextView(context)
                    itemSlotForCreateTextView.setBackgroundResource(recipeItem.recipe[index].item.imageId)
                    itemSlotForCreateTextView.text = recipeItem.recipe[index].count.toString()
                    itemSlotForCreateTextView.gravity = Gravity.BOTTOM
                    itemSlotForCreateTextView.setTextColor(Color.BLACK)
                    binding.wrapperRecipeItemLinearLayout.addView(itemSlotForCreateTextView)
                }

                with(binding) {
                    mainTextView.text = recipeItem.defence.toString()
                    var commaText = ",\n"
                    var commonText = ""
                    recipeItem.armorEffect.forEach {
                        if (it.nameText == recipeItem.armorEffect.last().nameText) commaText = ""
                        commonText += "${context.getText(it.nameText)}$commaText "
                        secondTextTextView.text = commonText
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmorHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemBinding.inflate(layoutInflater, parent, false)

        binding.createRecipeItemButton.setOnClickListener(this)

        return ArmorHolder(binding)
    }

    override fun getItemCount(): Int = armorList.size

    override fun onBindViewHolder(holder: ArmorHolder, position: Int) {
        val recipeArmor = armorList[position]

        holder.bind(recipeArmor)
    }

    override fun onClick(view: View) {
        val recipeItem = view.tag as Armor

        recipeItemCreateRepository.createItem(recipeItem)
    }
}