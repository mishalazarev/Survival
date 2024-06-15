package white.ball.survival.present.dialog_screen.notification.adapter

import android.content.Context
import android.graphics.Paint.Align
import android.text.Layout.Alignment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.NewsBlockBinding
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.repository.NotificationRepository

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationHolder>()  {

    var newsList: List<News> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class NotificationHolder(val binding: NewsBlockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsBlock: News) {
            with(binding) {
                captionNewTextView.setText(newsBlock.captionNews)
                mainTextTextView.text = newsBlock.mainText
                newsImageView.setImageResource(newsBlock.imageId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsBlockBinding.inflate(layoutInflater, parent, false)

        binding.wrapperNewsBlockConstraintLayout.setOnClickListener{}

        return NotificationHolder(binding)
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val newsBlock = newsList[position]

        holder.bind(newsBlock)
    }
}