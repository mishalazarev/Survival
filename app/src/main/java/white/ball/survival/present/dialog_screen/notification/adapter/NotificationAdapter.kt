package white.ball.survival.present.dialog_screen.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.NewsBlockBinding
import white.ball.survival.domain.model.News.NewsNotification

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationHolder>()  {

    var mNewsNotificationList: List<NewsNotification> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class NotificationHolder(val binding: NewsBlockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsNotificationBlock: NewsNotification) {
            with(binding) {
                captionNewTextView.setText(newsNotificationBlock.captionNews)
                mainTextTextView.text = newsNotificationBlock.mainText
                newsImageView.setImageResource(newsNotificationBlock.imageId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsBlockBinding.inflate(layoutInflater, parent, false)

        binding.wrapperNewsBlockConstraintLayout.setOnClickListener{}

        return NotificationHolder(binding)
    }

    override fun getItemCount(): Int = mNewsNotificationList.size

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val newsBlock = mNewsNotificationList[position]

        holder.bind(newsBlock)
    }
}