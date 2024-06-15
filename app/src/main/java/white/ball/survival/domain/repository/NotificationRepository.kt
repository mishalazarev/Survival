package white.ball.survival.domain.repository

import white.ball.survival.domain.model.News.NewsNotification
import white.ball.survival.domain.model.extension_model.ItemsSlot

interface NotificationRepository {

    fun loadNews()

    fun getNewsList(): List<NewsNotification>

    fun clearNews()

    fun addNotifyAboutNewLevel(newLevelByPlayer: Int)

    fun addNotifyFoodReady(newItemFood: ItemsSlot)

    fun addNotifyItemStole(newItemStole: ItemsSlot)

    fun addNotifyPhosphorusHasDimmed()

    fun notifyNewsListener()
}