package white.ball.survival.domain.service

import android.animation.PropertyValuesHolder
import android.view.View
import white.ball.survival.R

class AnimationAction(
    private var animationAttackXFinishFloat: Float = 0f,
    private var animationGetDamageXFinishFloat: Float = 0f,
    private var animationEffectYFinishFloat: Float = 0f,
    private var animationEffectAttackFinishFloat: Float = 0f
) {
    private lateinit var animationPlayerLongMoveXStart: PropertyValuesHolder
    private lateinit var animationPlayerLongMoveXFinish: PropertyValuesHolder

    private lateinit var animationPlayerShortMoveStart: PropertyValuesHolder
    private lateinit var animationPlayerShortMoveFinish: PropertyValuesHolder

    private lateinit var animationPlayerAttackEffectXStart: PropertyValuesHolder
    private lateinit var animationPlayerAttackEffectXFinish: PropertyValuesHolder

    private lateinit var animationAnimalLongMoveXStart: PropertyValuesHolder
    private lateinit var animationAnimalLongMoveXFinish: PropertyValuesHolder

    private lateinit var animationAnimalShortMoveStart: PropertyValuesHolder
    private lateinit var animationAnimalShortMoveFinish: PropertyValuesHolder

    private lateinit var animationAnimalAttackEffectXStart: PropertyValuesHolder
    private lateinit var animationAnimalAttackEffectXFinish: PropertyValuesHolder

    val animationAlphaEffect: PropertyValuesHolder = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

    // set current live objects move
    lateinit var currentAttackMoveXStart: PropertyValuesHolder
    lateinit var currentAttackMoveXFinish: PropertyValuesHolder

    lateinit var currentGetDamageXStart: PropertyValuesHolder
    lateinit var currentGetDamageXFinish: PropertyValuesHolder

    lateinit var currentAttackEffectXStart: PropertyValuesHolder
    lateinit var currentAttackEffectXFinish: PropertyValuesHolder

    lateinit var animationEffectMoveYStart: PropertyValuesHolder
    lateinit var animationEffectMoveYFinish: PropertyValuesHolder

    fun reloadMainPoints(
        animationAttackXFloatNew: Float,
        animationGetDamageXEndFloatNew: Float,
        animationEffectYEndFloatNew: Float,
        animationEffectAttackFinishFloatNew: Float
    ) {
        animationAttackXFinishFloat = animationAttackXFloatNew
        animationGetDamageXFinishFloat = animationGetDamageXEndFloatNew
        animationEffectYFinishFloat = animationEffectYEndFloatNew
        animationEffectAttackFinishFloat = animationEffectAttackFinishFloatNew
    }

    fun setMainPointAnimation() {
        animationPlayerLongMoveXStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, animationAttackXFinishFloat)
        animationPlayerLongMoveXFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, animationAttackXFinishFloat, 0f)

        animationPlayerShortMoveStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -animationGetDamageXFinishFloat)
        animationPlayerShortMoveFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -animationGetDamageXFinishFloat, 0f)

        animationPlayerAttackEffectXStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, animationEffectAttackFinishFloat)
        animationPlayerAttackEffectXFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, animationEffectAttackFinishFloat, 0f)

        animationAnimalLongMoveXStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -animationAttackXFinishFloat)
        animationAnimalLongMoveXFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -animationAttackXFinishFloat, 0f)

        animationAnimalShortMoveStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, animationGetDamageXFinishFloat)
        animationAnimalShortMoveFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, animationGetDamageXFinishFloat, 0f)

        animationAnimalAttackEffectXStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -animationEffectAttackFinishFloat)
        animationAnimalAttackEffectXFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -animationEffectAttackFinishFloat, 0f)

        animationEffectMoveYStart = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -animationEffectYFinishFloat)
        animationEffectMoveYFinish = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -animationEffectYFinishFloat, 0f)
    }

    fun setCloseStrikeAttackObject(attackObjectId: Int) {
        if (attackObjectId == R.id.player_image_view) {
            currentAttackMoveXStart = animationPlayerLongMoveXStart
            currentAttackMoveXFinish = animationPlayerLongMoveXFinish

            currentGetDamageXStart = animationAnimalShortMoveStart
            currentGetDamageXFinish = animationAnimalShortMoveFinish
        } else {
            currentAttackMoveXStart = animationAnimalLongMoveXStart
            currentAttackMoveXFinish = animationAnimalLongMoveXFinish

            currentGetDamageXStart = animationPlayerShortMoveStart
            currentGetDamageXFinish = animationPlayerShortMoveFinish
        }

    }

    fun setLongShotAttackObject(attackObjectId: Int) {
        if (attackObjectId == R.id.player_image_view) {
            currentAttackMoveXStart = animationPlayerShortMoveStart
            currentAttackMoveXFinish = animationPlayerShortMoveFinish

            currentGetDamageXStart = animationAnimalShortMoveStart
            currentGetDamageXFinish = animationAnimalShortMoveFinish

            currentAttackEffectXStart = animationPlayerAttackEffectXStart
            currentAttackEffectXFinish = animationPlayerAttackEffectXFinish
        } else {
            currentAttackMoveXStart = animationAnimalShortMoveStart
            currentAttackMoveXFinish = animationAnimalShortMoveFinish

            currentGetDamageXStart = animationPlayerShortMoveStart
            currentGetDamageXFinish = animationPlayerShortMoveFinish

            currentAttackEffectXStart = animationAnimalAttackEffectXStart
            currentAttackEffectXFinish = animationAnimalAttackEffectXFinish
        }
    }

}