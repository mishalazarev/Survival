package white.ball.survival.domain.model.extension_model

import white.ball.survival.R


enum class Effect(
    val nameText: Int,
    var damageValue: Int,
    val imageEffectResId: Int,
    var timeAction: Long,
    val flagLetterEffect: String
) {

    WEAKNESS(R.string.weakness,  0, R.drawable.effect_weakness,90_000,"WA"),
    POISONING(R.string.poisoning, 0, R.drawable.effect_poison,90_000,"PA"),
    STUN(R.string.stun, 0, R.drawable.effect_stun,0,"SA"),
    FIRE_BALL(R.string.fire_ball, 50, R.drawable.effect_fire_ball, 0, "FA"),

    HEALING(R.string.healing, 0, R.drawable.effect_healing, 90_000, "HN"),
    REGENERATION(R.string.regeneration, 0, R.drawable.effect_regeneration,90_000,"RN"),
    STOP_WEAKNESS(R.string.stop_weakness, 0, R.drawable.effect_resisting,0,"WS"),
    STOP_POISONING(R.string.stop_poisoning,0, R.drawable.effect_resisting,0,"PS"),

    NO_EFFECT(R.string.no_effect, 0, R.drawable.effect_damage,0,"NN");

    fun changeValueDamage(valueDamage: Int) {
        this.damageValue = valueDamage
    }
}
