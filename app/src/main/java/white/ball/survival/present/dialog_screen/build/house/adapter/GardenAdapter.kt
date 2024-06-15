package white.ball.survival.present.dialog_screen.build.house.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.R
import white.ball.survival.databinding.GardenBlockBinding
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.Plant

class GardenAdapter(
    private val itemRepository: BuildRepository,
    private val context: Context
) : RecyclerView.Adapter<GardenAdapter.GardenHolder>() {

    var gardenList: List<Plant> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GardenHolder(val binding: GardenBlockBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GardenHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GardenBlockBinding.inflate(layoutInflater, parent, false)

        return GardenHolder(binding)
    }

    override fun getItemCount(): Int = gardenList.size

    override fun onBindViewHolder(holder: GardenHolder, position: Int) {
        TODO("Not yet implemented")
    }

}