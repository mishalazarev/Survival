package white.ball.survival.domain.repository

import android.content.ClipData.Item
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.extension_model.ItemsSlot

interface NotificationRepository {

    fun loadNews()

    fun getNewsList(): List<News>

    fun clearNews()

    fun addNotifyAboutNewLevel(newLevelByPlayer: Int)

    fun addNotifyFoodReady(newItemFood: ItemsSlot)

    fun addNotifyItemStole(newItemStole: ItemsSlot)

    fun addNotifyPhosphorusHasDimmed()

    fun notifyNewsListener()
}