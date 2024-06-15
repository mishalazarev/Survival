package white.ball.survival.domain.repository

import white.ball.survival.domain.model.extension_model.Indicator
import white.ball.survival.domain.model.extension_model.IndicatorEndurance
import white.ball.survival.domain.model.extension_model.IndicatorHealth

interface IndicatorRepository<T : Indicator> {

    fun <T : IndicatorEndurance> T.makeStopEndurance(): Boolean

    fun <T : IndicatorHealth> T.poisoning(): Boolean

    fun T.wait()

}