package white.ball.survival.domain.repository

interface AbilityRepository {

    fun poisoning(): Int

    fun makeToFellWeak(): Boolean

    fun regeneration(): Boolean

}