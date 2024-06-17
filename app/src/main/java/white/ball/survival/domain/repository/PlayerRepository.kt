package white.ball.survival.domain.repository

import white.ball.survival.domain.model.News.NewsNotification
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.base_model.LiveObject
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.base_model.RecipeForItem
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.service.PlayerListener

interface PlayerRepository : LiveObject {

    // the methods are for fightScreen
    fun hit(): Effect

    fun superPunch(): Effect

    fun startEffect(effect: Effect, time: Int)

    fun surrender(animal: Animal): Boolean

    fun bowShot(): Effect

    fun scrollAttack(): Effect

    fun scrollRegeneration(): Effect

    // the backpack and other moves make with it
    fun setBuild(buildItem: ItemsSlot?)

    fun getPlayer(): Player

    fun showMyBackpack(): List<ItemsSlot>

    fun showMyPutOn(): List<ItemsSlot?>

    fun joinSimilarItemsSlot(itemsSlot: ItemsSlot)

    fun showRecipes() : Array<Weapon>

    fun createItem(recipeItem: RecipeForItem): Boolean

    fun takeItems(itemsSlots: MutableList<ItemsSlot>)

    fun throwItemInCurrentBackpackAway(items: ItemsSlot)

    fun takeFood(item: ItemsSlot): Boolean

    fun setPutOn(index: Int, itemsForPutOn: ItemsSlot?): Int

    fun divideIntoQualityGroups(item: ItemsSlot)

    fun throwAllItemsAway(items: ItemsSlot)

    fun throwItemInCurrentItemsPutOnAway(item: ItemsSlot)

    fun openShell(itemShell: ItemsSlot)

    fun openScroll(scrollOpening: ItemsSlot)

    // a characteristic player
    fun giveItemForLife(): ItemsSlot

    fun takeExp(expValue: Int)

    fun upParameter(keyParameter: Char)

    fun isUpDatePhysiologicalParameters(): Boolean

    fun addPlayerListener(playerListener: PlayerListener)

    // other moves
    fun showNewsList(): List<NewsNotification>

    fun clearNewsList()

    fun thiefStealItem()

    fun sleep(): Boolean

    fun moveAnotherLocation(): Boolean

    fun loadPlayer(newPlayer: Player)

    fun death()

}