package white.ball.survival.present.dialog_screen.victory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.ItemInBorderVictoryBinding
import white.ball.survival.domain.model.extension_model.ItemsSlot

class DropAdapter : RecyclerView.Adapter<DropAdapter.DropHolder>() {

    var itemsDropList: List<ItemsSlot> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class DropHolder(val binding: ItemInBorderVictoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemInBorderVictoryBinding.inflate(layoutInflater, parent, false)
        return DropHolder(binding)
    }

    override fun onBindViewHolder(holder: DropHolder, position: Int) {
        val itemsDrop = itemsDropList[position]

        holder.binding.run {
            imageItemDropImageView.setBackgroundResource(itemsDrop.item.imageId)
            imageItemDropImageView.tag = itemsDrop
            countDropTextView.text = "+${itemsDrop.count.toString()}"
            countDropTextView.tag = itemsDrop
        }
    }

    override fun getItemCount(): Int = itemsDropList.size

}