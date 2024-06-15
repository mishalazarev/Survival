package white.ball.survival.present.dialog_screen.user_guide.adapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.SectionNewsUpDateInUserGuideBinding
import white.ball.survival.domain.model.News.NewsUpDate

class NewsUpDateAdapter() : RecyclerView.Adapter<NewsUpDateAdapter.NewsUpDateHolder>() {

    var newsUpDate: List<NewsUpDate> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class NewsUpDateHolder(val binding: SectionNewsUpDateInUserGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsUpDate: NewsUpDate) {
            with(binding) {
                captionTextView.setText(newsUpDate.captionResId)
                dateTextView.setText(newsUpDate.dateResId)
                nameVersionTextView.setText(newsUpDate.nameVersionResId)
                mainTextView.setText(newsUpDate.mainTextResId)
                imageView.setImageResource(newsUpDate.imageResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsUpDateHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SectionNewsUpDateInUserGuideBinding.inflate(layoutInflater, parent, false)

        return NewsUpDateHolder(binding)
    }

    override fun getItemCount(): Int = newsUpDate.size

    override fun onBindViewHolder(holder: NewsUpDateHolder, position: Int) {
        val currentNewsUpDate = newsUpDate[position]

        holder.bind(currentNewsUpDate)
    }
}