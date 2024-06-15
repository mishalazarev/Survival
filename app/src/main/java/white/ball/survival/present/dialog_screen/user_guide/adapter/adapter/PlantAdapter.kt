package white.ball.survival.present.dialog_screen.user_guide.adapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.SectionPlantInUserGuideBinding
import white.ball.survival.domain.model.user_guide.DescriptionPlant

class PlantAdapter(
    private val context: Context
) : RecyclerView.Adapter<PlantAdapter.PlantHolder>() {

    var plantList: List<DescriptionPlant> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class PlantHolder(private val binding: SectionPlantInUserGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: DescriptionPlant) {
            var textEffectResult = ""

            with(binding) {
                captionNameTextView.setText(plant.namePlant)
                imageTextView.setBackgroundResource(plant.imageIdRes)
                nameLocationTextView.setText(plant.inNameLocation)
                nutritionValueTextView.text = plant.nutritionValue.toString()
                moistureValueTextView.text = plant.moistureValue.toString()

                plant.effects.forEach {
                    textEffectResult += if (it.nameText == plant.effects.last().nameText) {
                        "${context.getText(it.nameText)}"
                    } else {
                        "${context.getText(it.nameText)}, "
                    }
                }
                effectTextView.text = textEffectResult
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SectionPlantInUserGuideBinding.inflate(layoutInflater, parent, false)

        return PlantHolder(binding)
    }

    override fun getItemCount(): Int = plantList.size

    override fun onBindViewHolder(holder: PlantHolder, position: Int) {
        val plant = plantList[position]

        holder.bind(plant)
    }
}