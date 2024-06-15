package white.ball.survival.domain.navigator

import white.ball.survival.domain.model.location.Location

interface NavigatorByMap {

    fun onClickedLocationPressed(location: Location)

}