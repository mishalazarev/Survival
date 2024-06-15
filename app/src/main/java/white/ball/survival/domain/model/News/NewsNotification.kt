package white.ball.survival.domain.model.News

import white.ball.survival.R
import white.ball.survival.domain.model.extension_model.ItemsSlot

data class NewsNotification (
    var captionNews: Int = 0,
    var mainText: String = "",
    var imageId: Int = 0,
) {

    fun getLevelUpNews(levelByPlayer: Int): NewsNotification {
        return NewsNotification (
            R.string.caption_new_level,
            "Поздравляем! Вы достигли $levelByPlayer уровень и получили за это 10 очков обучения.",
            R.drawable.notify_new_level
        )
    }

    fun getFoodCookedNews(itemFoodName: String): NewsNotification {
        return NewsNotification (
            R.string.caption_food_cook,
            "Вы приготовили $itemFoodName в количестве 1 шт.",
            R.drawable.notify_food_cooked
        )
    }

    fun getItemStoleNews(itemStoleName: String): NewsNotification {
        return NewsNotification (
            R.string.caption_item_stole,
            "Этой ночью у вас воры украли $itemStoleName в количестве 1 шт.\n" +
                    "Для того чтобы эта ситуация не повторялась необходимо добыть Фосфор.",
            R.drawable.notify_item_stole
        )
    }

    fun getPhosphorusDimNews(): NewsNotification {
        return NewsNotification (
            R.string.caption_phosphorus_dim,
            "О нет! Фосфор перестал гореть, проверьте остались ли у вас еще в наличии.",
            R.drawable.notify_phosphorus_dim
        )
    }

    fun plantWantsToDrink(namePlant: String): NewsNotification {
        return NewsNotification (
            R.string.plant_wants_to_drink,
            "Ваше растение $namePlant хочет пить, пожалуйста используйте для этого бутыль с водой.",
            R.drawable.notify_plant_wants_to_drink
        )
    }

    fun plantGiveFruits(namePlant: String): NewsNotification {
        return NewsNotification (
            R.string.plant_give_fruits,
            "Ваше растение $namePlant дало плоды, вы можете их забрать в огороде.",
            R.drawable.notify_fruits
        )
    }

    fun plantCameToLive(namePlant: String): NewsNotification {
        return NewsNotification (
            R.string.plant_became_to_live,
            "Оо нет!! Ваше растение ожило, берегитесь иначе оно на вас нападет!!!",
            R.drawable.notify_animal_fruit
        )
    }

    fun plantBecomeAdult(namePlant: Int): NewsNotification {
        return NewsNotification (
            R.string.plant_become_adult,
            "Ваше растение дало плоды, пожалуйста заберите их.",
            R.drawable.notify_plant_growed
        )
    }

    fun waterExtract(): NewsNotification {
        return NewsNotification (
            R.string.water_extract,
            "В колодце было добыто вода в количестве 1 шт.",
            R.drawable.notify_extract_water
        )
    }

    fun wellIsFull(): NewsNotification {
        return NewsNotification (
            R.string.well_is_full,
            "Добыча воды прекращена, пожалуйста заберите воду, чтобы колодец продолжил работу.",
            R.drawable.notify_bag_is_full
        )
    }

    fun getResource(resourceArray: Array<ItemsSlot>, nameItems: Array<String>): NewsNotification {
        var textResult = ""
        var index = 0
        resourceArray.forEach {
            textResult += "\n- ${nameItems[index]} ${it.count} шт."
            index++
        }
        return NewsNotification (
            R.string.got_resource,
            textResult,
            R.drawable.notify_bag_is_full
        )
    }
}

