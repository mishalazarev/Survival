package white.ball.survival.domain.repository

import white.ball.survival.domain.model.base_model.RecipeForItem

interface RecipeItemCreateRepository {
    fun createItem(recipeItem: RecipeForItem)

}