package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.weapon.Weapon

interface RecipeItemCreateRepository {
    fun createItem(recipeItem: Slot)

}