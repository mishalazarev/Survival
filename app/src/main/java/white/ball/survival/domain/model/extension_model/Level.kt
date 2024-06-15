package white.ball.survival.domain.model.extension_model

class Level {

    private val levelUp = mutableMapOf (
        1 to 50,
        2 to 100,
        3 to 250,
        4 to 500,
        5 to 750,
        6 to 1_100,
        7 to 1_400,
        8 to 1_700,
        9 to 2_000,
        10 to 2_300,
        11 to 2_700,
        12 to 3_100,
        13 to 3_500,
        14 to 3_900,
        15 to 5_000,
        16 to 6_000,
        17 to 7_000
    )

    fun checkOnNewLevel(exp: Int): Int {
        levelUp.forEach{
                if (it.value >= exp) return it.key
        }
        return 0
    }

    fun getMaxExp(level: Int): Int {
        levelUp.forEach{
            if (it.key == level) return it.value
        }
        return 50
    }

}