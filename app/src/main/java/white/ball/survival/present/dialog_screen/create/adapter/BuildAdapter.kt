package white.ball.survival.present.dialog_screen.create.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.RecipeItemHouseBinding
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Hut
import white.ball.survival.domain.model.build.StoneHouse
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.RecipeItemCreateRepository

class BuildAdapter(
    private val recipeItemCreateRepository: RecipeItemCreateRepository,
    private val context: Context
): RecyclerView.Adapter<BuildAdapter.BuildHolder>(), OnClickListener {

    private val well = Well()
    private val bonfire = Bonfire()
    private val hut = Hut()
    private val woodHouse = WoodHouse()
    private val stoneHouse = StoneHouse()

    var buildList: List<BuildRepository> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class BuildHolder(val binding: RecipeItemHouseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipeItem: BuildRepository) {
            with(binding) {
                recipeItemTextView.setBackgroundResource(recipeItem.imageId)
                createRecipeItemButton.tag = recipeItem
                nameRecipeItemTextView.setText(recipeItem.nameItem)
                recipeItemTextView.text = "+1"
                fieldForPlantTextView.text = if (recipeItem is Hut || recipeItem is WoodHouse || recipeItem is StoneHouse) {
                    PLUS_TEXT
                } else {
                    MINUS_TEXT
                }
                isCookingTextView.text = if (recipeItem.placeForCook != null) {
                    PLUS_TEXT
                } else {
                    MINUS_TEXT
                }
                isGettingWaterTextView.text = if (recipeItem.placeForWater != null) {
                    PLUS_TEXT
                } else {
                    MINUS_TEXT
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemHouseBinding.inflate(layoutInflater, parent, false)

        binding.createRecipeItemButton.setOnClickListener(this)

        return BuildHolder(binding)
    }

    override fun getItemCount(): Int = buildList.size
    override fun onBindViewHolder(holder: BuildHolder, position: Int) {
        val recipeBuild = buildList[position]

        holder.bind(recipeBuild)

        if (holder.binding.wrapperRecipeItemLinearLayout.isEmpty()) {
            for (index in recipeBuild.recipe.indices) {
                val itemSlotForCreateTextView = TextView(context)
                itemSlotForCreateTextView.setBackgroundResource(recipeBuild.recipe[index].item.imageId)
                itemSlotForCreateTextView.text = recipeBuild.recipe[index].count.toString()
                itemSlotForCreateTextView.gravity = Gravity.BOTTOM
                itemSlotForCreateTextView.setTextColor(Color.BLACK)
                holder.binding.wrapperRecipeItemLinearLayout.addView(itemSlotForCreateTextView)
            }
        }
    }

    companion object {
        @JvmStatic
        val PLUS_TEXT = "+"
        @JvmStatic
        val MINUS_TEXT = "-"
    }

    override fun onClick(view: View?) {
        val recipeItem = view!!.tag as BuildRepository

        recipeItemCreateRepository.createItem(recipeItem)
    }
}