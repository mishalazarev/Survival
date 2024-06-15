package white.ball.survival.present.dialog_screen.user_guide.adapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.SectionAnimalInUserGuideBinding
import white.ball.survival.domain.model.user_guide.DescriptionAnimal

class AnimalAdapter : RecyclerView.Adapter<AnimalAdapter.AnimalHolder>() {

    var animalList: List<DescriptionAnimal> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class AnimalHolder(val  binding: SectionAnimalInUserGuideBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(animal: DescriptionAnimal) {
            with(binding) {
                captionNameTextView.setText(animal.nameAnimal)
                imageTextView.setBackgroundResource(animal.imageIdRes)
                nameLocationTextView.setText(animal.inNameLocation)
                effectTextView.setText(animal.effects)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SectionAnimalInUserGuideBinding.inflate(layoutInflater, parent, false)

        return AnimalHolder(binding)
    }

    override fun getItemCount(): Int = animalList.size

    override fun onBindViewHolder(holder: AnimalHolder, position: Int) {
        val animal = animalList[position]

        holder.bind(animal)
    }

}