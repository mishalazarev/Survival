package white.ball.survival.present.screen.main_screen.game_play.fight

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import white.ball.survival.R
import white.ball.survival.databinding.FragmentFightBinding
import white.ball.survival.domain.model.animal.Animal
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.EscapeType
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.domain.repository.GameTimerControllerRepository
import white.ball.survival.domain.service.AnimationAction
import white.ball.survival.present.dialog_screen.backpack.BackpackDialogFragment
import white.ball.survival.present.dialog_screen.end_game.EndGameDialogFragment
import white.ball.survival.present.dialog_screen.lose.DefeatDialogFragment
import white.ball.survival.present.dialog_screen.victory.VictoryDialogFragment
import white.ball.survival.present.screen.main_screen.game_play.GamePlayFragment
import white.ball.survival.present.screen.main_screen.game_play.GamePlayFragment.Companion.TIME_UP_DATE_PHYSIOLOGICAL
import white.ball.survival.present.screen.main_screen.game_play.fight.view_model.FightViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator
import kotlin.random.Random

class FightFragment : Fragment() {

    private lateinit var binding: FragmentFightBinding

    private lateinit var commonTime: CommonTimerController

    private lateinit var animationAction: AnimationAction

    private val colorHealthRegeneration by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.regeneration
        )
    }
    private val colorHealthDefault by lazy { ContextCompat.getColor(requireContext(), R.color.red) }
    private val colorHealthPoison by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.poisoning
        )
    }

    private val colorEnduranceRegeneration by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.yellow_light
        )
    }
    private val colorEnduranceDefault by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.yellow
        )
    }
    private val colorEnduranceWeak by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.weakness
        )
    }

    private lateinit var checkOnNextStep: Job
    private lateinit var animalStepJob: Job

    private val backpackDialogFragment = BackpackDialogFragment()

    private lateinit var victoryDialogScreen: VictoryDialogFragment
    private lateinit var defeatDialogScreen: DefeatDialogFragment
    private lateinit var endGameDialogScreen: EndGameDialogFragment
    private var isAlreadyLaunchDialogFragmentFlag = false

    private lateinit var hitByPunchSound: MediaPlayer
    private lateinit var hitBySwordSound: MediaPlayer
    private lateinit var archerySound: MediaPlayer
    private lateinit var hitByFireBallSound: MediaPlayer
    private lateinit var regenerationMagicSound: MediaPlayer
    private lateinit var negativeMagicSound: MediaPlayer

    private lateinit var gamePlaySound: MediaPlayer
    private lateinit var fightWithBossSound: MediaPlayer
    private lateinit var endGameSound: MediaPlayer

    private val viewModel: FightViewModel by viewModelCreator {
        FightViewModel(
            playerRepository = it.playerAction,
            interactionWithEnvironmentRepository = it.interactionWithEnvironment,
            gameTimeRepository = it.gameTimeAction,
        )
    }

    private val textHintArray: Array<String> = arrayOf(
        "Режим боя представлен один на один",
        "Слева указаны ваше здоровье и выносливость, а справа вашего противника." +
                "\nЗаметьте! 1 урон = 1 выносливость. В случае недостачи выносливости, вы будете промахиваться по противнику.",
        "Каждый ход может длиться максимально 15 секунд, постарайтесь уложиться в это время.",
        "А это основные действия для атаки, сейчас досупно только лишь одно действие, но все это поправимо." +
                "\nВо время хода можно использовать только один из них.",
        "Во время вашего хода можно менять оружие, доспехи, а если вы очень шустрый, то и подкрепиться можно успеть. Все это не считается за ход.",
        "Когда вы понимаете что вам не поплечу этот противник, то вы можете всегда использовать белый флаг, но не думайте что это будет бесплатно. " +
                "И не каждый противник будет хотеть вас отпустить.",
        "Не забывай что твой главный противник это <<Поработитель>>, его нужно найти и проучить." +
                "\nОх уж и заболтался я, хорошего боя, пусть победит сильнейший!"

        )
    private lateinit var animateActionIdList: Array<Array<View>>

    private lateinit var animateIcons: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFightBinding.inflate(inflater, container, false)

        animationAction = AnimationAction(
            3f * requireContext().resources.displayMetrics.densityDpi,
            0.5f * requireContext().resources.displayMetrics.densityDpi,
            0.5f * requireContext().resources.displayMetrics.densityDpi,
            3f * requireContext().resources.displayMetrics.densityDpi
        )

        animationAction.setMainPointAnimation()

        viewModel.loadAnimal()

        viewModel.animal.observe(viewLifecycleOwner) {
            binding.animalImageView.setImageResource(it.imageId)
            binding.animalHealthIndicatorProgressBar.max = it.health.indicator.maxPercent
            binding.animalHealthIndicatorProgressBar.progress = it.health.indicator.percent
            binding.animalEnduranceIndicatorProgressBar.max = it.endurance.indicator.maxPercent
            binding.animalEnduranceIndicatorProgressBar.progress = it.endurance.indicator.percent
        }

        viewModel.player.observe(viewLifecycleOwner) {
            binding.playerHealthIndicatorProgressBar.max = it.health.indicator.maxPercent
            binding.playerHealthIndicatorProgressBar.progress = it.health.indicator.percent
            binding.playerEnduranceIndicatorProgressBar.max = it.endurance.indicator.maxPercent
            binding.playerEnduranceIndicatorProgressBar.progress = it.endurance.indicator.percent
        }

        viewModel.location.observe(viewLifecycleOwner) {
            binding.backgroundFightConstraintLayout.setBackgroundResource(it.imageForFightResId)
        }

        with(binding) {
            backpackImageButton.setOnClickListener {
                hidIconsOnSomeTime()
                navigator().onLaunchDialogFragmentPressed(backpackDialogFragment)
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.player.value!!.isHintsInFightIncluded) {
            binding.hintTextView.visibility = View.VISIBLE
            binding.weaponAttackImageButton.isEnabled = false

            binding.hintTextView.text = textHintArray[viewModel.countHint]
            viewModel.countHint += 1

            binding.hintTextView.setOnClickListener {
                if (textHintArray.size == viewModel.countHint) {
                    binding.hintTextView.visibility = View.INVISIBLE
                    binding.weaponAttackImageButton.isEnabled = true

                    commonTime = CommonTimerController(viewModel.gameTime.value!!)
                    commonTime.startGameTime()
                    commonTime.startFightTime()
                    commonTime.gameDaysLeft = viewModel.gameTime.value!!.gameDaysLeft
                    viewModel.player.value!!.isHintsInFightIncluded = false
                } else {
                    binding.hintTextView.text = textHintArray[viewModel.countHint]
                    alphaAnimationOfIcon(viewModel.countHint)
                    viewModel.countHint += 1
                }
            }
        } else {
        commonTime = CommonTimerController(viewModel.gameTime.value!!)
        commonTime.startGameTime()
        commonTime.startFightTime()
        commonTime.gameDaysLeft = viewModel.gameTime.value!!.gameDaysLeft
        }

        hitByPunchSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.hit_by_punch)
        hitBySwordSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.hit_by_sword)
        archerySound = MediaPlayer.create(requireActivity().applicationContext, R.raw.archery)
        hitByFireBallSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.fire_ball)
        regenerationMagicSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.regeneration_magic)
        negativeMagicSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.negative_magic)

        with(binding) {
            loadArrowAttackButton()
            loadScrollAttackButton()
            loadSuperPunchButton()

            weaponAttackImageButton.setOnClickListener {
                commonTime.stopFightTime()
                hidOrShowSkills()
                animationCloseStrike(
                    attackObjectImageView = playerImageView,
                    getDamageObjectImageView = animalImageView,
                    effectGetDamage = wrapperGetDamageAnimalLinearLayout,
                    effectObject = viewModel.hitByPlayer()
                )
                lifecycleScope.launch {
                    delay(GET_DAMAGE_TIME)
                    if (viewModel.player.value!!.placeForPutOn[0] == null) {
                        hitByPunchSound.start()
                    } else if (viewModel.player.value!!.placeForPutOn[0] != null && (viewModel.player.value!!.placeForPutOn[0]!!.item == Weapon.MACE ||
                                viewModel.player.value!!.placeForPutOn[0]!!.item == Weapon.WOODEN_SWORD || viewModel.player.value!!.placeForPutOn[0]!!.item == Weapon.BOW)
                    ) {
                        hitByPunchSound.start()
                    } else {
                        hitBySwordSound.start()
                    }
                }
                viewModel.notifyChanges()
            }

            superPunchImageButton.setOnClickListener {
                val resultEffect = viewModel.superPunchByPlayer()
                if (resultEffect.damageValue == 0) {
                    Toast.makeText(
                        requireContext(),
                        R.string.count_endurance_not_enough,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    commonTime.stopFightTime()
                    hidOrShowSkills()
                    animationCloseStrike(
                        attackObjectImageView = playerImageView,
                        getDamageObjectImageView = animalImageView,
                        effectGetDamage = wrapperGetDamageAnimalLinearLayout,
                        effectObject = resultEffect
                    )
                    lifecycleScope.launch {
                        delay(GET_DAMAGE_TIME)
                        if (viewModel.player.value!!.placeForPutOn[0]!!.item.nameItem == Weapon.MACE.nameItem ||
                            viewModel.player.value!!.placeForPutOn[0]!!.item.nameItem == Weapon.WOODEN_SWORD.nameItem
                        ) {
                            hitByPunchSound.start()
                        } else {
                            hitBySwordSound.start()
                        }
                    }
                }
                viewModel.notifyChanges()
            }

            arrowAttackImageButton.setOnClickListener {
                val resultArrowAttack = viewModel.bowShotByPlayer()
                if (resultArrowAttack.damageValue == 0) {
                    Toast.makeText(requireContext(), R.string.count_endurance_not_enough, Toast.LENGTH_SHORT).show()
                } else {
                    commonTime.stopFightTime()
                    hidOrShowSkills()
                    animationLongShot(
                        attackObjectImageView = playerImageView,
                        attackEffectImageView = playerAttackEffectImageView,
                        getDamageObjectImageView = animalImageView,
                        effectGetDamage = wrapperGetDamageAnimalLinearLayout,
                        attackEffect = resultArrowAttack,
                    )
                    viewModel.notifyChanges()
                    lifecycleScope.launch {
                        delay(GET_DAMAGE_TIME)
                        archerySound.start()
                    }
                }
            }

            scrollAttackImageButton.setOnClickListener {
                commonTime.stopFightTime()
                hidOrShowSkills()
                when (viewModel.player.value!!.placeForPutOn[3]!!.item.imageId) {
                    R.drawable.item_scroll_fire_ball -> {
                        animationLongShot(
                            attackObjectImageView = playerImageView,
                            attackEffectImageView = playerAttackEffectImageView,
                            getDamageObjectImageView = animalImageView,
                            effectGetDamage = wrapperGetDamageAnimalLinearLayout,
                            attackEffect = viewModel.scrollAttackByPlayer()
                        )

                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            hitByFireBallSound.start()
                        }
                    }

                    R.drawable.item_scroll_regeneration -> {
                        animationScrollMagicRegeneration(wrapperGetDamagePlayerLinearLayout,playerEffectImageView, Effect.REGENERATION)
                        viewModel.scrollRegenerationByPlayer()
                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            regenerationMagicSound.start()
                        }
                    }

                    else -> {
                        animationScrollMagic(
                            attackMagicEffectImageView = playerAttackEffectImageView,
                            getDamageObjectImageView = animalImageView,
                            effectGetDamage = wrapperGetDamageAnimalLinearLayout,
                            attackEffect = viewModel.scrollAttackByPlayer(),
                        )
                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            negativeMagicSound.start()
                        }
                    }
                }
                viewModel.notifyChanges()
            }

            surrenderImageButton.setOnClickListener {
                val resultSurrender = viewModel.surrender(viewModel.animal.value!!)

                if (viewModel.animal.value!!.chanceEscapePlayer == EscapeType.SURRENDER && !resultSurrender) {
                    Toast.makeText(
                        requireContext(),
                        R.string.empty_backpack_surrender,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showToastAnswerAnimalOnSurrender(resultSurrender)
                }
            }
        }

        if (viewModel.animal.value!!.nameAnimal == R.string.enslaver) {
            fightWithBossSound =
                MediaPlayer.create(requireActivity().applicationContext, R.raw.fight_with_boss)
            fightWithBossSound.start()
            fightWithBossSound.isLooping = true

            endGameSound = MediaPlayer.create(requireActivity().applicationContext, R.raw.end_game)
        } else {
            gamePlaySound =
                MediaPlayer.create(requireActivity().applicationContext, R.raw.game_play)
            gamePlaySound.start()
            gamePlaySound.isLooping = true
        }

    }


    override fun onStop() {
        super.onStop()
        commonTime.stopCommonTimes()
//        animalStepJob.cancel()

        if (viewModel.animal.value!!.nameAnimal == R.string.enslaver) {
            endGameSound.stop()
            endGameSound.isLooping = false
        } else {
            gamePlaySound.stop()
            gamePlaySound.isLooping = false
            gamePlaySound.release()
        }

        hitByPunchSound.release()
        hitBySwordSound.release()
        archerySound.release()
        hitByFireBallSound.release()
        regenerationMagicSound.release()
        negativeMagicSound.release()

    }

    private fun loadHaveEffectDeny(nameText: String) {
        if (nameText == viewModel.animal.value!!.name) {
            binding.haveEffectAnimalLinearLayout.removeAllViews()
            viewModel.animal.value!!.effectHaveDeny.forEach {
                val imageEffect = ImageView(requireContext())
                imageEffect.setImageResource(it.imageEffectResId)
                binding.haveEffectAnimalLinearLayout.addView(imageEffect)
            }
        } else {
            binding.haveEffectPlayerLinearLayout.removeAllViews()
            viewModel.player.value!!.effectHaveDeny.forEach {
                val imageEffect = ImageView(requireContext())
                imageEffect.setImageResource(it.imageEffectResId)
                binding.haveEffectPlayerLinearLayout.addView(imageEffect)
            }
        }
        viewModel.notifyChanges()
    }

    private fun animalStep() {
        animalStepJob = lifecycleScope.launch {
            commonTime.startFightTime()
            if (binding.weaponAttackImageButton.isVisible) hidOrShowSkills()
            val randomTime = Random.nextLong(ONE_SECOND, TIME_STEP_ANIMAL)
            delay(randomTime)
            commonTime.stopFightTime()
            if (viewModel.animal.value!!.nameAnimal == Animal.ENSLAVER.nameAnimal) {
                when (val animalEffect = viewModel.scrollAttackByAnimal()) {
                    Effect.FIRE_BALL -> {
                        animationLongShot(
                            attackObjectImageView = binding.animalAttackEffectImageView,
                            attackEffectImageView = binding.animalAttackEffectImageView,
                            getDamageObjectImageView = binding.playerImageView,
                            effectGetDamage = binding.wrapperGetDamagePlayerLinearLayout,
                            attackEffect = animalEffect
                        )

                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            hitByFireBallSound.start()
                        }
                    }

                    Effect.REGENERATION, Effect.HEALING -> {
                        viewModel.scrollRegenerationByAnimal(animalEffect)
                        animationScrollMagicRegeneration(binding.wrapperGetDamageAnimalLinearLayout,binding.animalGetEffectImageView, animalEffect)
                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            regenerationMagicSound.start()
                        }
                    }

                    else -> {
                        animationScrollMagic(
                            attackMagicEffectImageView = binding.animalAttackEffectImageView,
                            getDamageObjectImageView = binding.playerImageView,
                            effectGetDamage = binding.wrapperGetDamagePlayerLinearLayout,
                            attackEffect = animalEffect,
                        )

                        lifecycleScope.launch {
                            delay(EFFECT_MAGIC_WAIT)
                            negativeMagicSound.start()
                        }
                    }
                }
            } else {
                animationCloseStrike(
                    binding.animalImageView,
                    binding.playerImageView,
                    binding.wrapperGetDamagePlayerLinearLayout,
                    viewModel.hitByAnimal()
                )

                lifecycleScope.launch {
                    delay(GET_DAMAGE_TIME)
                    hitByPunchSound.start()
                }
            }

            binding.haveEffectPlayerLinearLayout.removeAllViews()
            viewModel.player.value!!.effectHaveDeny.forEach {
                val imageEffect = ImageView(requireContext())
                imageEffect.setImageResource(it.imageEffectResId)
                binding.haveEffectPlayerLinearLayout.addView(imageEffect)
            }
//            binding.haveDenyEffectByPlayerTextView.text = "${viewModel.player.value!!.effectHaveDeny}"
        }
    }


    private fun animationCloseStrike(
        attackObjectImageView: ImageView,
        getDamageObjectImageView: ImageView,
        effectGetDamage: LinearLayout,
        effectObject: Effect
    ) {
        val weaponByPlayerImageView = binding.weaponForPlayerImageView

        animationAction.setCloseStrikeAttackObject(attackObjectImageView.id)

        val animationMoveStart = ObjectAnimator.ofPropertyValuesHolder(
            attackObjectImageView,
            animationAction.currentAttackMoveXStart
        ).apply {
            duration = ONE_SECOND
            interpolator = AnticipateOvershootInterpolator()
        }

        val animationMoveFinish = ObjectAnimator.ofPropertyValuesHolder(
            attackObjectImageView,
            animationAction.currentAttackMoveXFinish
        ).apply {
            duration = ONE_SECOND
            interpolator = AnticipateOvershootInterpolator()
        }

        val animationMoveWeaponStart = ObjectAnimator.ofPropertyValuesHolder(
            weaponByPlayerImageView,
            animationAction.currentAttackMoveXStart
        ).apply {
            duration = ONE_SECOND
            interpolator = AnticipateOvershootInterpolator()
        }
        val animationMoveWeaponFinish = ObjectAnimator.ofPropertyValuesHolder(
            weaponByPlayerImageView,
            animationAction.currentAttackMoveXFinish
        ).apply {
            duration = ONE_SECOND
            interpolator = AnticipateOvershootInterpolator()
        }

        val animationGetDamageWeaponStart = ObjectAnimator.ofPropertyValuesHolder(
            weaponByPlayerImageView,
            animationAction.currentGetDamageXStart
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = AnticipateInterpolator()
            start()
        }

        val animationGetDamageWeaponFinish = ObjectAnimator.ofPropertyValuesHolder(
            weaponByPlayerImageView,
            animationAction.currentGetDamageXFinish
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = AnticipateInterpolator()
            start()
        }

        setGetDamageEffect(effectGetDamage, effectObject, getDamageObjectImageView)

        val animationMoveAndAlphaIconEffect = ObjectAnimator.ofPropertyValuesHolder(
            effectGetDamage,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationGetDamageStart = ObjectAnimator.ofPropertyValuesHolder(
            getDamageObjectImageView,
            animationAction.currentGetDamageXStart
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = AnticipateInterpolator()
            start()
        }

        val animationGetDamageFinish = ObjectAnimator.ofPropertyValuesHolder(
            getDamageObjectImageView,
            animationAction.currentGetDamageXFinish
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = AnticipateInterpolator()
            start()
        }

        AnimatorSet().apply {
            if (attackObjectImageView.id == R.id.player_image_view) {
                play(animationMoveStart)
                    .with(animationMoveAndAlphaIconEffect)
                    .before(animationMoveFinish)
                    .with(animationGetDamageStart)
                    .before(animationGetDamageFinish)

                play(animationMoveWeaponStart)
                    .before(animationMoveWeaponFinish)

            } else {
                play(animationMoveStart)
                    .with(animationMoveAndAlphaIconEffect)
                    .before(animationMoveFinish)
                    .with(animationGetDamageStart)
                    .before(animationGetDamageFinish)
                    .with(animationGetDamageWeaponStart)
                    .before(animationGetDamageWeaponFinish)

            }
            start()
        }

        checkOnNextStep = lifecycleScope.launch {

            delay(ONE_SECOND)
            effectGetDamage.visibility = View.INVISIBLE
            commonTime.startFightTime()

            if (!viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN) && !viewModel.animal.value!!.effectHaveDeny.contains(
                    Effect.STUN
                )
            )
                viewModel.repeatAttackAnimal++

            if (viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN)) {
                animalStep()
                viewModel.player.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.animal.value!!.effectHaveDeny.contains(Effect.STUN)) {
                hidOrShowSkills()
                viewModel.animal.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.repeatAttackAnimal % 2 == 0 && checkToStopFight()) {
                animalStep()
            } else {
                hidOrShowSkills()
            }
        }
    }

    private fun animationLongShot(
        attackObjectImageView: ImageView,
        attackEffectImageView: ImageView,
        getDamageObjectImageView: ImageView,
        effectGetDamage: LinearLayout,
        attackEffect: Effect
    ) {
        val weaponByPlayerImageView = binding.weaponForPlayerImageView
        animationAction.setLongShotAttackObject(attackObjectImageView.id)

        val animationMoveStart = ObjectAnimator.ofPropertyValuesHolder(
            attackObjectImageView,
            animationAction.currentAttackMoveXStart
        ).apply {
            duration = 700
            interpolator = AnticipateOvershootInterpolator()
        }

        val animationMoveFinish = ObjectAnimator.ofPropertyValuesHolder(
            attackObjectImageView,
            animationAction.currentAttackMoveXFinish
        ).apply {
            duration = 700
            interpolator = AnticipateInterpolator()
        }

        val animationAttackEffectStart = ObjectAnimator.ofPropertyValuesHolder(
            attackEffectImageView,
            animationAction.currentAttackEffectXStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = FastOutLinearInInterpolator()
        }


        val animationAttackEffectFinish = ObjectAnimator.ofPropertyValuesHolder(
            attackEffectImageView,
            animationAction.currentAttackEffectXFinish,
            animationAction.animationAlphaEffect
        ).apply {
            duration = GET_DAMAGE_TIME
            interpolator = FastOutLinearInInterpolator()
        }

        val animationMoveWeaponStart: ObjectAnimator
        val animationMoveWeaponFinish: ObjectAnimator
        if (attackObjectImageView.id == R.id.player_image_view) {
            animationMoveWeaponStart = ObjectAnimator.ofPropertyValuesHolder(
                weaponByPlayerImageView,
                animationAction.currentAttackMoveXStart
            ).apply {
                duration = 700
                interpolator = AnticipateOvershootInterpolator()
            }
            animationMoveWeaponFinish = ObjectAnimator.ofPropertyValuesHolder(
                weaponByPlayerImageView,
                animationAction.currentAttackMoveXFinish
            ).apply {
                duration = 700
                interpolator = AnticipateInterpolator()
            }
        } else {
            animationMoveWeaponStart = ObjectAnimator.ofPropertyValuesHolder(
                weaponByPlayerImageView,
                animationAction.currentGetDamageXStart
            ).apply {
                duration = 700
                interpolator = AnticipateInterpolator()
            }
            animationMoveWeaponFinish = ObjectAnimator.ofPropertyValuesHolder(
                weaponByPlayerImageView,
                animationAction.currentGetDamageXFinish
            ).apply {
                duration = 700
                interpolator = AnticipateInterpolator()
            }
        }

        setGetDamageEffect(effectGetDamage, attackEffect, getDamageObjectImageView)
        attackEffectImageView.visibility = View.VISIBLE

        when (attackEffect) {
            Effect.NO_EFFECT -> {
                attackEffectImageView.setImageResource(R.drawable.recipe_item_arrow)
                if (attackObjectImageView.id == R.id.player_image_view) {
                    attackEffectImageView.rotation = 45F
                } else {
                    attackEffectImageView.rotation = -45F
                }
            }

            Effect.FIRE_BALL -> {
                attackEffectImageView.setImageResource(R.drawable.effect_fire_ball)
                if (attackObjectImageView.id == R.id.player_image_view) {
                    attackEffectImageView.rotation = -90F
                } else {
                    attackEffectImageView.rotation = 90F
                }
            }

            Effect.POISONING -> {
                attackEffectImageView.setImageResource(R.drawable.recipe_item_poisoned_arrow)
                if (attackObjectImageView.id == R.id.player_image_view) {
                    attackEffectImageView.rotation = 45F
                } else {
                    attackEffectImageView.rotation = -45F
                }
            }

            else -> throw IllegalArgumentException(
                "Attack Effect is unknown ${
                    getString(
                        attackEffect.nameText
                    )
                }"
            )
        }

        val animationMoveAndAlphaIconEffect = ObjectAnimator.ofPropertyValuesHolder(
            effectGetDamage,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationGetDamageStart =
            ObjectAnimator.ofPropertyValuesHolder(
                getDamageObjectImageView,
                animationAction.currentGetDamageXStart
            ).apply {
                duration = 700
                interpolator = AnticipateInterpolator()
                start()
            }

        val animationGetDamageFinish = ObjectAnimator.ofPropertyValuesHolder(
            getDamageObjectImageView,
            animationAction.currentGetDamageXFinish
        )
            .apply {
                duration = 700
                interpolator = AnticipateInterpolator()
                start()
            }


        AnimatorSet().apply {
            if (attackObjectImageView.id == R.id.player_image_view) {
                play(animationMoveStart)
                    .with(animationMoveAndAlphaIconEffect)
                    .before(animationMoveFinish)
                    .with(animationAttackEffectStart)
                    .with(animationGetDamageStart)
                    .before(animationGetDamageFinish)
                    .before(animationAttackEffectFinish)
                    .with(animationMoveWeaponStart)
                    .before(animationMoveWeaponFinish)
            } else {
                play(animationMoveStart)
                    .with(animationMoveAndAlphaIconEffect)
                    .before(animationMoveFinish)
                    .with(animationAttackEffectStart)
                    .with(animationGetDamageStart)
                    .before(animationGetDamageFinish)
                    .with(animationMoveWeaponStart)
                    .before(animationAttackEffectFinish)
                    .before(animationMoveWeaponFinish)
            }

            start()
        }

        lifecycleScope.launch {
            delay(GET_DAMAGE_TIME)
            attackEffectImageView.visibility = View.INVISIBLE
        }

        checkOnNextStep = lifecycleScope.launch {

            delay(ONE_SECOND)
            effectGetDamage.visibility = View.INVISIBLE

            commonTime.startFightTime()

            if (!viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN) && !viewModel.animal.value!!.effectHaveDeny.contains(
                    Effect.STUN
                )
            )
                viewModel.repeatAttackAnimal++

            if (viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN)) {
                animalStep()
                viewModel.player.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.animal.value!!.effectHaveDeny.contains(Effect.STUN)) {
                hidOrShowSkills()
                viewModel.animal.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.repeatAttackAnimal % 2 == 0 && checkToStopFight()) {
                animalStep()
            } else {
                hidOrShowSkills()
            }
        }
    }

    private fun animationScrollMagic(
        attackMagicEffectImageView: ImageView,
        getDamageObjectImageView: ImageView,
        effectGetDamage: LinearLayout,
        attackEffect: Effect
    ) {
        val animationMagicMoveAndAlphaIconEffect = ObjectAnimator.ofPropertyValuesHolder(
            attackMagicEffectImageView,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationMagicMoveAndAlphaIconEffectFinish = ObjectAnimator.ofPropertyValuesHolder(
            attackMagicEffectImageView,
            animationAction.animationEffectMoveYFinish
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationMoveAndAlphaIconEffect = ObjectAnimator.ofPropertyValuesHolder(
            effectGetDamage,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        setGetDamageEffect(effectGetDamage, attackEffect, getDamageObjectImageView)
        attackMagicEffectImageView.visibility = View.VISIBLE
        attackMagicEffectImageView.setImageResource(R.drawable.effect_magic)

        AnimatorSet().apply {
            play(animationMagicMoveAndAlphaIconEffect)
                .with(animationMoveAndAlphaIconEffect)
                .before(animationMagicMoveAndAlphaIconEffectFinish)

            start()
        }

        checkOnNextStep = lifecycleScope.launch {

            delay(ONE_SECOND)
            effectGetDamage.visibility = View.INVISIBLE
            attackMagicEffectImageView.visibility = View.INVISIBLE

            commonTime.startFightTime()

            if (!viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN) && !viewModel.animal.value!!.effectHaveDeny.contains(
                    Effect.STUN
                )
            )
                viewModel.repeatAttackAnimal++

            if (viewModel.player.value!!.effectHaveDeny.contains(Effect.STUN)) {
                animalStep()
                viewModel.player.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.animal.value!!.effectHaveDeny.contains(Effect.STUN)) {
                hidOrShowSkills()
                viewModel.animal.value!!.effectHaveDeny.remove(Effect.STUN)
            } else if (viewModel.repeatAttackAnimal % 2 == 0 && checkToStopFight()) {
                animalStep()
            } else {
                hidOrShowSkills()
            }
        }
    }

    private fun animationScrollMagicRegeneration(wrapperGetDamageLinearLayout: LinearLayout,attackMagicEffectImageView: ImageView, magicEffect: Effect) {
        val animationMagicMoveAndAlphaIconEffectStart = ObjectAnimator.ofPropertyValuesHolder(
            wrapperGetDamageLinearLayout,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationMagicMoveAndAlphaIconEffectFinish = ObjectAnimator.ofPropertyValuesHolder(
            wrapperGetDamageLinearLayout,
            animationAction.animationEffectMoveYStart,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val animationMagicMoveAndAlphaIconEffectReturn = ObjectAnimator.ofPropertyValuesHolder(
            wrapperGetDamageLinearLayout,
            animationAction.animationEffectMoveYFinish,
            animationAction.animationAlphaEffect
        ).apply {
            duration = ONE_SECOND
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        wrapperGetDamageLinearLayout.visibility = View.VISIBLE
        wrapperGetDamageLinearLayout[1].visibility = View.INVISIBLE
        attackMagicEffectImageView.setImageResource(R.drawable.effect_magic)

        AnimatorSet().apply {
            play(animationMagicMoveAndAlphaIconEffectStart)
                .after(animationMagicMoveAndAlphaIconEffectFinish)
        }.start()

        checkOnNextStep = lifecycleScope.launch {

            delay(ONE_SECOND)
            wrapperGetDamageLinearLayout.visibility =View.INVISIBLE
            attackMagicEffectImageView.setImageResource(magicEffect.imageEffectResId)
            wrapperGetDamageLinearLayout.visibility = View.VISIBLE
            delay(ONE_SECOND)
            wrapperGetDamageLinearLayout.visibility = View.INVISIBLE
            wrapperGetDamageLinearLayout[1].visibility = View.VISIBLE

            commonTime.startFightTime()

            viewModel.repeatAttackAnimal++

            if (viewModel.repeatAttackAnimal % 2 == 0 && checkToStopFight()) {
                animalStep()
            } else {
                hidOrShowSkills()
            }

            AnimatorSet().apply {
                play(animationMagicMoveAndAlphaIconEffectReturn)
            }.start()

        }
    }

    private fun alphaAnimationOfIcon(indexIcon: Int) {
        animateActionIdList = arrayOf(
            arrayOf(),
            arrayOf(binding.wrapperPlayerIndicatorsLinearLayout, binding.wrapperEnemyIndicatorsLinearLayout),
            arrayOf(binding.fightTimeTextView),
            arrayOf(binding.weaponAttackImageButton, binding.superPunchImageButton, binding.scrollAttackImageButton, binding.arrowAttackImageButton),
            arrayOf(binding.backpackImageButton),
            arrayOf(binding.surrenderImageButton),
            arrayOf())

        if (animateActionIdList[indexIcon - 1].isNotEmpty()) {
            animateIcons.cancel()
            animateActionIdList[indexIcon - 1].forEach {
                it.alpha = 1f
            }
        }


        animateIcons = AnimatorSet().apply {
            animateActionIdList[indexIcon].forEach {
                play(
                ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
                ).apply {
                    duration = GamePlayFragment.ANIMATE_ALPHA_ICON
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    start()
                })
            }
            start()
        }
    }

    private fun setGetDamageEffect(
        effectGetDamage: LinearLayout,
        effectObject: Effect,
        getDamageObjectImageView: ImageView
    ) {
        effectGetDamage.visibility = View.VISIBLE
        (effectGetDamage[1] as TextView).text = "-${effectObject.damageValue}"

        if (getDamageObjectImageView.id == R.id.animal_image_view) {
            (effectGetDamage[0] as ImageView).setImageResource(effectObject.imageEffectResId)
        } else {
            (effectGetDamage[0] as ImageView).setImageResource(effectObject.imageEffectResId)
        }
    }


    private fun checkToStopFight(): Boolean {
        if (viewModel.animal.value!!.health.indicator.percent <= 0 && viewModel.animal.value!!.nameAnimal == R.string.enslaver) {
            makeDisableMove()
            commonTime.stopCommonTimes()
            checkOnNextStep.cancel()
            endGameDialogScreen = EndGameDialogFragment.newInstance(false)
            if (!isAlreadyLaunchDialogFragmentFlag) {
                isAlreadyLaunchDialogFragmentFlag = !isAlreadyLaunchDialogFragmentFlag
                animalStepJob.cancel()
                navigator().onLaunchDialogFragmentPressed(endGameDialogScreen)
            }
            fightWithBossSound.isLooping = false
            fightWithBossSound.stop()
            endGameSound.start()
            endGameSound.isLooping = true
        } else if (viewModel.player.value!!.health.indicator.percent <= 0 && viewModel.animal.value!!.nameAnimal == R.string.enslaver) {
            makeDisableMove()
            commonTime.stopCommonTimes()
            checkOnNextStep.cancel()
            endGameDialogScreen = EndGameDialogFragment.newInstance(true)
            if (!isAlreadyLaunchDialogFragmentFlag) {
                isAlreadyLaunchDialogFragmentFlag = !isAlreadyLaunchDialogFragmentFlag
                animalStepJob.cancel()
                navigator().onLaunchDialogFragmentPressed(endGameDialogScreen)
            }
            fightWithBossSound.isLooping = false
            fightWithBossSound.stop()
            endGameSound.start()
            endGameSound.isLooping = true
        } else if (viewModel.animal.value!!.health.indicator.percent <= 0 && viewModel.animal.value!!.nameAnimal != R.string.enslaver) {
            makeDisableMove()
            commonTime.stopCommonTimes()
            victoryDialogScreen =
                VictoryDialogFragment.newInstance(viewModel.getCurrentNameAnimal())
            if (!isAlreadyLaunchDialogFragmentFlag) {
                isAlreadyLaunchDialogFragmentFlag = !isAlreadyLaunchDialogFragmentFlag
                animalStepJob.cancel()
                navigator().onLaunchDialogFragmentPressed(victoryDialogScreen)
            }
            checkOnNextStep.cancel()
            return false
        } else if (viewModel.player.value!!.health.indicator.percent <= 0 && viewModel.animal.value!!.nameAnimal != R.string.enslaver) {
            makeDisableMove()
            commonTime.stopCommonTimes()
            defeatDialogScreen = DefeatDialogFragment.newInstance(0, false, true)
            if (!isAlreadyLaunchDialogFragmentFlag) {
                isAlreadyLaunchDialogFragmentFlag = !isAlreadyLaunchDialogFragmentFlag
                animalStepJob.cancel()
                navigator().onLaunchDialogFragmentPressed(defeatDialogScreen)
            }
            checkOnNextStep.cancel()
            return false
        }
        return true
    }

    private fun loadArrowAttackButton() {
        val bowInPutOnBoolean: Boolean = if (viewModel.showPutOnItems()[0] == null) {
            false
        } else (viewModel.showPutOnItems()[0]!!.item as Weapon).itemUse == ItemUse.BOW
        val arrowInPutOnBoolean: Boolean = if (viewModel.showPutOnItems()[1] == null) {
            false
        } else (viewModel.showPutOnItems()[1] != null && (viewModel.showPutOnItems()[1]!!.item as Weapon).itemUse == ItemUse.ARROW)

        binding.arrowAttackImageButton.isEnabled = bowInPutOnBoolean && arrowInPutOnBoolean
    }

    private fun loadScrollAttackButton() {
        binding.scrollAttackImageButton.isEnabled =
            viewModel.player.value!!.placeForPutOn[3] != null
    }

    private fun loadSuperPunchButton() {
        val weaponPutOnByPlayer = viewModel.player.value!!.placeForPutOn[0]
        binding.superPunchImageButton.isEnabled =
            weaponPutOnByPlayer != null && (weaponPutOnByPlayer.item as Weapon).itemEffect != Effect.NO_EFFECT
    }

    private fun loadSurrender() {
        binding.surrenderImageButton.isEnabled =
            !(viewModel.animal.value!!.chanceEscapePlayer == EscapeType.NO_SURRENDER || viewModel.player.value!!.backpack.isEmpty())
    }

    private fun loadArmorAndWeaponByPlayer() {
        with(viewModel.player.value!!) {

            if (placeForPutOn[0] != null) {
                binding.weaponForPlayerImageView.setBackgroundResource(placeForPutOn[0]!!.item.imageId)
            } else {
                binding.weaponForPlayerImageView.setBackgroundResource(0)
            }

            if (placeForPutOn[2] != null) {
                binding.playerImageView.setBackgroundResource((placeForPutOn[2]!!.item as Armor).playerInArmor)
            } else {
                binding.playerImageView.setBackgroundResource(R.drawable.animal_player)
            }
        }
    }

    private fun loadIndicatorsByEveryone() {
        binding.animalHealthIndicatorProgressBar.progress =
            viewModel.animal.value!!.health.indicator.percent
        binding.animalEnduranceIndicatorProgressBar.progress =
            viewModel.animal.value!!.endurance.indicator.percent

        binding.playerHealthIndicatorProgressBar.progress =
            viewModel.player.value!!.health.indicator.percent
        binding.playerEnduranceIndicatorProgressBar.progress =
            viewModel.player.value!!.endurance.indicator.percent
    }

    private fun changeColorOnIndicatorByPlayer() {
        with(binding) {
            if (viewModel.player.value!!.health.isPoisoning) {
                playerHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthPoison)
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            } else if (viewModel.player.value!!.health.isRegeneration) {
                playerHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthRegeneration)
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            } else {
                playerHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthDefault)
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            }

            if (viewModel.player.value!!.endurance.isWeakYet) {
                playerEnduranceIndicatorProgressBar.progressDrawable.setTint(colorEnduranceWeak)
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            } else if (viewModel.player.value!!.endurance.isRegeneration) {
                playerEnduranceIndicatorProgressBar.progressDrawable.setTint(
                    colorEnduranceRegeneration
                )
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            } else {
                playerEnduranceIndicatorProgressBar.progressDrawable.setTint(colorEnduranceDefault)
                loadHaveEffectDeny(viewModel.player.value!!.namePlayer)
            }

        }
    }

    private fun changeColorOnIndicatorByAnimal() {
        with(binding) {
            if (viewModel.animal.value!!.health.isPoisoning) {
                animalHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthPoison)
                loadHaveEffectDeny(viewModel.animal.value!!.name)
            } else if (viewModel.animal.value!!.health.isRegeneration) {
                animalHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthRegeneration)
                loadHaveEffectDeny(viewModel.animal.value!!.name)
            } else {
                animalHealthIndicatorProgressBar.progressDrawable.setTint(colorHealthDefault)
                loadHaveEffectDeny(viewModel.animal.value!!.name)
            }

            if (viewModel.animal.value!!.endurance.isWeakYet) {
                animalEnduranceIndicatorProgressBar.progressDrawable.setTint(colorEnduranceWeak)
                loadHaveEffectDeny(viewModel.animal.value!!.name)
            } else {
                animalEnduranceIndicatorProgressBar.progressDrawable.setTint(colorEnduranceDefault)
                loadHaveEffectDeny(viewModel.animal.value!!.name)
            }
        }
    }

    private fun hidOrShowSkills() {
        with(binding) {
            if (weaponAttackImageButton.isVisible) {
                arrowAttackImageButton.isVisible = false
                superPunchImageButton.isVisible = false
                scrollAttackImageButton.isVisible = false
                weaponAttackImageButton.isVisible = false
                backpackImageButton.isVisible = false
                surrenderImageButton.isVisible = false
            } else {
                arrowAttackImageButton.isVisible = true
                superPunchImageButton.isVisible = true
                scrollAttackImageButton.isVisible = true
                weaponAttackImageButton.isVisible = true
                backpackImageButton.isVisible = true
                surrenderImageButton.isVisible = true
            }
        }
    }

    private fun hidIconsOnSomeTime() {
        binding.arrowAttackImageButton.visibility = View.INVISIBLE
        binding.superPunchImageButton.visibility = View.INVISIBLE
        binding.scrollAttackImageButton.visibility = View.INVISIBLE
        binding.weaponAttackImageButton.visibility = View.INVISIBLE
        binding.backpackImageButton.visibility = View.INVISIBLE

        lifecycleScope.launch {
            delay(SOME_TIME)
            binding.arrowAttackImageButton.visibility = View.VISIBLE
            binding.superPunchImageButton.visibility = View.VISIBLE
            binding.scrollAttackImageButton.visibility = View.VISIBLE
            binding.weaponAttackImageButton.visibility = View.VISIBLE
            binding.backpackImageButton.visibility = View.VISIBLE
        }
    }

    private fun makeDisableMove() {
        binding.arrowAttackImageButton.isEnabled = false
        binding.superPunchImageButton.isEnabled = false
        binding.scrollAttackImageButton.isEnabled = false
        binding.weaponAttackImageButton.isEnabled = false
        binding.backpackImageButton.isEnabled = false
    }

    private fun showToastAnswerAnimalOnSurrender(isSurrenderSuccess: Boolean) {
        if (isSurrenderSuccess) {
            val playerItem = viewModel.giftForLife()
            commonTime.stopCommonTimes()
            animalStepJob = lifecycleScope.launch {}
            animalStepJob.cancel()
            makeDisableMove()
            defeatDialogScreen = DefeatDialogFragment.newInstance(
                imageResId = playerItem.item.imageId,
                isAlive = true,
                isFightScreen = true
            )
            if (!isAlreadyLaunchDialogFragmentFlag) {
                isAlreadyLaunchDialogFragmentFlag = !isAlreadyLaunchDialogFragmentFlag
                navigator().onLaunchDialogFragmentPressed(defeatDialogScreen)
            }
        }
    }

    inner class CommonTimerController(gameTime: GameTime) : GameTimerControllerRepository {

        override lateinit var gameTime: CountDownTimer
        var fightTime: CountDownTimer? = null

        override var gameDaysLeft: Int = 0
        override var elapsedTime: Long = 0
        private var checkOnRepeatAboutSleepSoonBoolean = true
        private var checkOnRepeatAboutYouCanSleep = true
        private var indexByPlayerEffects = 0
        private var indexByAnimalEffects = 0
        private val effectsByPlayer = viewModel.player.value!!.effectHaveDeny
        private val effectsByAnimal = viewModel.animal.value!!.effectHaveDeny

        init {
            gameTime.also {
                elapsedTime = it.gameTimeValue
                gameDaysLeft = it.gameDaysLeft
            }
        }

        fun startFightTime() {
            fightTime?.cancel()

            fightTime = object : CountDownTimer(ROUND_TIME, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished % 60_000 / 1000).toInt()
                    binding.fightTimeTextView.text = seconds.toString()

                    checkToStopFight()

                    loadArrowAttackButton()
                    loadScrollAttackButton()
                    loadSuperPunchButton()
                    loadArmorAndWeaponByPlayer()
                    loadSurrender()

                    viewModel.startCooking()
                    viewModel.startAntiThief()
                    viewModel.startExtractingWater()
                    viewModel.startGrowingPlant()

                    if (binding.fightTimeTextView.text == "0") {
                        viewModel.player.value!!.effectHaveDeny.remove(Effect.STUN)
                        viewModel.animal.value!!.effectHaveDeny.remove(Effect.STUN)
                        if (binding.weaponAttackImageButton.isVisible) hidOrShowSkills()
                        animalStep()
                    }
                }

                override fun onFinish() {}
            }.start()
        }

        fun stopFightTime() {
            fightTime?.cancel()
        }

        override fun stopGameTime() {
            gameTime.cancel()
        }

        override fun startGameTime() {
            gameTime = object : CountDownTimer(elapsedTime, ONE_SECOND) {
                override fun onTick(seconds: Long) {
//                    binding.gameTimeTextView.text = "${seconds / ONE_SECOND}"
                    elapsedTime = seconds
                    viewModel.setGameTimeParameters(gameDaysLeft, elapsedTime)

                    while (effectsByPlayer.size != indexByPlayerEffects) {
                        if (effectsByPlayer.size < indexByPlayerEffects) indexByPlayerEffects = 0
                        if (effectsByPlayer.size == 0) break

                        viewModel.startEffectByPlayer(
                            effectsByPlayer[indexByPlayerEffects],
                            (seconds / ONE_SECOND).toInt()
                        )
                        indexByPlayerEffects++
                    }
                    if (effectsByPlayer.size <= indexByPlayerEffects) indexByPlayerEffects = 0

                    if (seconds / GamePlayFragment.SECOND == 30L && checkOnRepeatAboutSleepSoonBoolean) {
                        Toast.makeText(activity, R.string.warning_about_sleep, Toast.LENGTH_SHORT)
                            .show()
                        checkOnRepeatAboutSleepSoonBoolean = !checkOnRepeatAboutSleepSoonBoolean
                    } else if (seconds / GamePlayFragment.SECOND == TIME_FOR_SLEEP && checkOnRepeatAboutYouCanSleep) {
                        Toast.makeText(
                            activity,
                            R.string.warning_about_you_can_sleep,
                            Toast.LENGTH_SHORT
                        ).show()
                        checkOnRepeatAboutYouCanSleep = false
                    }

                    if (effectsByAnimal.size == 0 && effectsByPlayer.size == 0) {
                        Effect.values().forEach {
                            it.timeAction = 90_000
                        }
                    }

                    while (effectsByAnimal.size != indexByAnimalEffects) {
                        if (effectsByAnimal.size < indexByAnimalEffects) indexByAnimalEffects = 0
                        if (effectsByAnimal.size == 0) break

                        viewModel.startEffectByAnimal(
                            effectsByAnimal[indexByAnimalEffects],
                            (seconds / ONE_SECOND).toInt()
                        )
                        indexByAnimalEffects++
                    }
                    if (effectsByAnimal.size <= indexByAnimalEffects) indexByAnimalEffects = 0

                    if (elapsedTime.toInt() % TIME_UP_DATE_PHYSIOLOGICAL == 0 && elapsedTime.toInt() != 0) {
                        viewModel.upDatePhysiologicalParameters()
                        loadIndicatorsByEveryone()
                    }

                    changeColorOnIndicatorByPlayer()
                    changeColorOnIndicatorByAnimal()
                }

                override fun onFinish() {
                    sleep()
                    startGameTime()
                }

            }.start()
        }

        override fun getGameTimeValueNow(): Long = elapsedTime

        override fun sleep() {
            lifecycleScope.launch {
                gameDaysLeft -= 1
                elapsedTime = GamePlayFragment.DAY_END_TIME
                viewModel.gameTime.value!!.gameSaveFlag = true
                checkOnRepeatAboutSleepSoonBoolean = true
                checkOnRepeatAboutYouCanSleep = true
                viewModel.setGameTimeParameters(gameDaysLeft, DAY_END_TIME)
                delay(4_500)
            }
        }

        fun stopCommonTimes() {
            fightTime!!.cancel()
            gameTime.cancel()
        }
    }

    companion object {
        @JvmStatic
        val TAG = "tag"

        @JvmStatic
        val ONE_SECOND: Long = 1_000

        @JvmStatic
        val SOME_TIME: Long = 250

        @JvmStatic
        val ROUND_TIME: Long = 16_000

        @JvmStatic
        val TIME_STEP_ANIMAL: Long = 3_000

        @JvmStatic
        val GET_DAMAGE_TIME: Long = 500

        @JvmStatic
        val EFFECT_MAGIC_WAIT: Long = 250

        @JvmStatic
        val DAY_END_TIME: Long = 380_000

        @JvmStatic
        val TIME_FOR_SLEEP = 250L
    }
}

