package white.ball.survival.present.dialog_screen.create.view_model

import androidx.lifecycle.ViewModel
import white.ball.survival.domain.model.base_model.RecipeForItem
import white.ball.survival.domain.repository.PlayerRepository

class CreateViewModel(
    private val playerRepository: PlayerRepository
): ViewModel() {

    fun createRecipeItem(recipeItem: RecipeForItem): Boolean {
        return playerRepository.createItem(recipeItem)
    }

}