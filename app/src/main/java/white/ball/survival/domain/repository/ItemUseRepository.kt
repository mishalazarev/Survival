package white.ball.survival.domain.repository

import white.ball.survival.domain.model.extension_model.ItemsSlot

interface ItemUseRepository {

    fun setBuild(itemBuild: ItemsSlot?)

    fun takeFood(food: ItemsSlot)

    fun joinSimilarItems(itemsSlot: ItemsSlot)

    fun openShell(itemShell: ItemsSlot)
    
    fun openScroll(itemScroll: ItemsSlot)

    fun setPutOnItem(itemsForPutOn: ItemsSlot)

    fun setIntoCook(itemsForCook: ItemsSlot)

    fun setAntiThief(itemPhosphorus: ItemsSlot)

    fun growPlant(itemSeed: ItemsSlot)

    fun giveWaterPlant(itemWater: ItemsSlot)

    fun findOutInformation(items: ItemsSlot)

    fun divideIntoQualityGroups(items: ItemsSlot)

    fun threwItAway(items: ItemsSlot)

    fun threwAllItemsAway(items: ItemsSlot)

}