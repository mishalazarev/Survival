package white.ball.survival.domain.model.base_model

import white.ball.survival.domain.model.extension_model.ItemsSlot

interface RecipeForItem : SlotForItem {
    val recipe: List<ItemsSlot>
}