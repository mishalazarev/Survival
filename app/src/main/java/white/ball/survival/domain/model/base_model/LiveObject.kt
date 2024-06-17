package white.ball.survival.domain.model.base_model

import white.ball.survival.domain.model.extension_model.Effect

interface LiveObject {

    fun useAbility(): Effect

    fun upDateInformation()
}