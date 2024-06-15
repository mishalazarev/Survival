package white.ball.survival.domain.service

import android.content.SharedPreferences
import white.ball.survival.domain.model.News.News
import white.ball.survival.domain.model.armor.Armor
import white.ball.survival.domain.model.base_model.GameTime
import white.ball.survival.domain.model.base_model.Item
import white.ball.survival.domain.model.base_model.Player
import white.ball.survival.domain.model.base_model.Slot
import white.ball.survival.domain.model.build.Bonfire
import white.ball.survival.domain.model.build.Hut
import white.ball.survival.domain.model.build.StoneHouse
import white.ball.survival.domain.model.build.Well
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.extension_model.Effect
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.model.plant.AloePlant
import white.ball.survival.domain.model.plant.FruitAnimalPlant
import white.ball.survival.domain.model.plant.MeatTree
import white.ball.survival.domain.model.plant.OakTree
import white.ball.survival.domain.model.plant.WheatPlant
import white.ball.survival.domain.model.weapon.Weapon
import white.ball.survival.domain.repository.BuildRepository

class Progress {

    private lateinit var currentGamePlayProgress: SharedPreferences

    private val well = Well()
    private val bonfire = Bonfire()
    private val hut = Hut()
    private val woodHouse = WoodHouse()
    private val stoneHouse = StoneHouse()

    fun init(newProgress: SharedPreferences) {
        currentGamePlayProgress = newProgress
    }

    fun clearProgress() {
        with(currentGamePlayProgress.edit()) {
            clear()
            commit()
        }
    }

    fun saveProgress(
        newLocations: List<Location>,
        newProgressPlayer: Player,
        newGameTime: GameTime) {
        // Player
        with(currentGamePlayProgress.edit()) {
            putString(NAME_PLAYER_KEY, newProgressPlayer.namePlayer)
            putBoolean(SATIETY_AND_THIRST_SETTING_FLAG_KEY, newProgressPlayer.satietyAndThirstSettingFlag)
            putBoolean(HINTS_FLAG_IN_GAME_PLAY_KEY, newProgressPlayer.isHintsInGamePlayIncluded)
            putBoolean(HINTS_FLAG_IN_FIGHT_KEY, newProgressPlayer.isHintsInFightIncluded)
            putInt(IMAGE_PLAYER_KEY, newProgressPlayer.imagePlayer)
            putInt(HEALTH_VALUE_KEY, newProgressPlayer.health.indicator.percent)
            putInt(HEALTH_MAX_KEY, newProgressPlayer.health.indicator.maxPercent)
            putInt(ENDURANCE_VALUE_KEY, newProgressPlayer.endurance.indicator.percent)
            putInt(ENDURANCE_MAX_KEY, newProgressPlayer.endurance.indicator.maxPercent)
            putInt(DAMAGE_VALUE_KEY, newProgressPlayer.damage)
            putInt(SATIETY_VALUE_KEY, newProgressPlayer.satiety.indicator.percent)
            putInt(THIRST_VALUE_KEY, newProgressPlayer.thirst.indicator.percent)
            setNewsList(newProgressPlayer.news)
            setBackpackList(newProgressPlayer.backpack)
            setPlacePutOnList(newProgressPlayer.placeForPutOn)
            setEffectHaveDeny(newProgressPlayer.effectHaveDeny)
            putInt(LEVEL_KEY, newProgressPlayer.level)
            putInt(EXP_KEY, newProgressPlayer.exp)
            putInt(STUDY_POINTS_KEY, newProgressPlayer.studyPoints)
            commit()
        }

        // GameTime
        with(currentGamePlayProgress.edit()) {
            putLong(GAME_TIME_VALUE_KEY, newGameTime.gameTimeValue)
            putInt(GAME_DAYS_LEFT_KEY, newGameTime.gameDaysLeft)
            putBoolean(GAME_SAVE_FLAG_KEY, newGameTime.gameSaveFlag)
            commit()
        }

        // Locations
        with(currentGamePlayProgress.edit()) {
            for (currentLocation in Location.entries) {
                putBoolean(IS_PLAYER_FIND_HERE_KEY + currentLocation.nameLocationId, currentLocation.isPlayerFindHere)
                setBuild(currentLocation)
            }
            commit()
        }
    }

    fun getPlayerProgress(): Player {
        val progressPlayer = Player()
        with(currentGamePlayProgress) {
            progressPlayer.namePlayer = getString(NAME_PLAYER_KEY, "").toString()
            progressPlayer.satietyAndThirstSettingFlag = getBoolean(SATIETY_AND_THIRST_SETTING_FLAG_KEY, false)
            progressPlayer.isHintsInGamePlayIncluded = getBoolean(HINTS_FLAG_IN_GAME_PLAY_KEY, false)
            progressPlayer.isHintsInFightIncluded = getBoolean(HINTS_FLAG_IN_FIGHT_KEY, false)
            progressPlayer.imagePlayer = getInt(IMAGE_PLAYER_KEY, 0)
            progressPlayer.health.indicator.percent = getInt(HEALTH_VALUE_KEY, 0)
            progressPlayer.health.indicator.maxPercent = getInt(HEALTH_MAX_KEY, 0)
            progressPlayer.endurance.indicator.percent = getInt(ENDURANCE_VALUE_KEY, 0)
            progressPlayer.endurance.indicator.maxPercent = getInt(ENDURANCE_MAX_KEY, 0)
            progressPlayer.damage = getInt(DAMAGE_VALUE_KEY, 0)
            progressPlayer.satiety.indicator.percent = getInt(SATIETY_VALUE_KEY, 0)
            progressPlayer.thirst.indicator.percent = getInt(THIRST_VALUE_KEY, 0)
            progressPlayer.news = getNewsList(getInt(NEWS_SIZE_KEY, 0))
            progressPlayer.backpack = getBackpackList(getInt(BACKPACK_SIZE_KEY, 0))
            progressPlayer.placeForPutOn = getPlacePutOnList()
            progressPlayer.effectHaveDeny = getEffectHaveDeny(getInt(EFFECT_HAVE_DENY_SIZE_KEY, 0))
            progressPlayer.level = getInt(LEVEL_KEY, 0)
            progressPlayer.exp = getInt(EXP_KEY, 0)
            progressPlayer.studyPoints = getInt(STUDY_POINTS_KEY, 0)
        }
        return progressPlayer
    }

    fun getLocationListProgress(): List<Location> {
        with(currentGamePlayProgress) {
            for (currentLocation in Location.entries) {
                currentLocation.isPlayerFindHere = getBoolean(IS_PLAYER_FIND_HERE_KEY + currentLocation.nameLocationId, false)
                currentLocation.build = getBuild(currentLocation)
            }
        }
        return Location.entries
    }

    private fun getBuild(currentLocation: Location): BuildRepository? {
        with(currentGamePlayProgress) {
            val nameLocation = getInt(NAME_BUILD_KEY + currentLocation.nameLocationId, 0)
            if (nameLocation == 0) return null

            when(nameLocation) {
                well.nameItem -> {
                    currentLocation.build = Well()
                    getWater(currentLocation)
                    getAntiThief(currentLocation)
                }
                bonfire.nameItem -> {
                    currentLocation.build = Bonfire()
                    getCook(currentLocation)
                    getAntiThief(currentLocation)
                }
                hut.nameItem -> {
                    currentLocation.build = Hut()
                    getCook(currentLocation)
                    getWater(currentLocation)
                    getAntiThief(currentLocation)
                    getFruit(currentLocation)
                    getPlant(currentLocation)
                }
                woodHouse.nameItem -> {
                    currentLocation.build = WoodHouse()
                    getCook(currentLocation)
                    getWater(currentLocation)
                    getAntiThief(currentLocation)
                    getFruit(currentLocation)
                    getPlant(currentLocation)

                }
                stoneHouse.nameItem -> {
                    currentLocation.build = StoneHouse()
                    getCook(currentLocation)
                    getWater(currentLocation)
                    getAntiThief(currentLocation)
                    getFruit(currentLocation)
                    getPlant(currentLocation)
                }
            }
        }
        return currentLocation.build
    }

    private fun setBuild(currentLocation: Location) {
        if (currentLocation.build == null) return

        with(currentGamePlayProgress.edit()) {
            putInt(NAME_BUILD_KEY + currentLocation.nameLocationId, currentLocation.build!!.nameItem)
            when(currentLocation.build!!.nameItem) {
                well.nameItem -> {
                    setWater(currentLocation)
                    setAntiThief(currentLocation)
                }
                bonfire.nameItem -> {
                    setPlaceForCook(currentLocation)
                    setAntiThief(currentLocation)
                }
                hut.nameItem, woodHouse.nameItem, stoneHouse.nameItem -> {
                    setPlaceForCook(currentLocation)
                    setWater(currentLocation)
                    setAntiThief(currentLocation)
                    if (currentLocation.build!!.placeForFruit != null) {
                        putInt(NAME_PLACE_FOR_FRUIT_KEY + currentLocation.nameLocationId, currentLocation.build!!.placeForFruit!!.item.nameItem)
                        putInt(SIZE_PLACE_FOR_FRUIT_KEY + currentLocation.nameLocationId, currentLocation.build!!.placeForFruit!!.count)
                    }
                    setPlant(currentLocation)
                }
            }
            commit()
        }
    }

    private fun getCook(currentLocation: Location) {
        val currentPlaceForCook = currentLocation.build!!.placeForCook!!
        with(currentGamePlayProgress) {
            for (index in currentPlaceForCook.indices) {
                val nameItemKey = NAME_PLACE_FOR_COOK_KEY + index + currentLocation.nameLocationId
                val sizeItemKey = SIZE_PLACE_FOR_COOK_KEY + index + currentLocation.nameLocationId

                val nameItem = getInt(nameItemKey, 0)

                if (nameItem != 0) {
                    loop@for (currentItem in Item.entries) {
                        if (currentItem.nameItem == nameItem) {
                            currentPlaceForCook[index] = ItemsSlot(currentItem, getInt(sizeItemKey, 0))
                            break@loop
                        }
                    }
                }
            }
            currentLocation.build!!.cooking!!.percent = getInt(COOKING_PROGRESS_KEY + currentLocation.nameLocationId, 0)
        }
    }

    private fun getPlant(currentLocation: Location) {
        with(currentGamePlayProgress) {
            val namePlant = getInt(NAME_PLANT_KEY + currentLocation.nameLocationId, 0)
            if (namePlant != 0) {
                val currentPlant = currentLocation.build!!.plant
                val aloePlant = AloePlant()
                val fruitAnimalPlant = FruitAnimalPlant()
                val meatTree = MeatTree()
                val oakTree = OakTree()
                val wheatPlant = WheatPlant()

                when (namePlant) {
                    aloePlant.namePlant -> {
                        currentLocation.build!!.plant = aloePlant
                    }
                    fruitAnimalPlant.namePlant -> {
                        currentLocation.build!!.plant = fruitAnimalPlant
                    }
                    meatTree.namePlant -> {
                        currentLocation.build!!.plant = meatTree
                    }
                    oakTree.namePlant -> {
                        currentLocation.build!!.plant = oakTree
                    }
                    wheatPlant.namePlant -> {
                        currentLocation.build!!.plant = wheatPlant
                    }
                }

                currentLocation.build!!.plant!!.currentLevel = getInt(CURRENT_LEVEL_KEY + currentLocation.nameLocationId, 0)
                currentLocation.build!!.plant!!.indicatorGrow.percent = getInt(INDICATORr_GROW_KEY + currentLocation.nameLocationId, 0)
                currentLocation.build!!.plant!!.isPlantWatered = getBoolean(IS_PLANT_WATERED_KEY + currentLocation.nameLocationId, false)
            }
        }
    }

    private fun setPlant(currentLocation: Location) {
        if (currentLocation.build!!.plant == null) return
        with(currentGamePlayProgress.edit()) {
            val currentPlant = currentLocation.build!!.plant!!

            putInt(NAME_PLANT_KEY + currentLocation.nameLocationId, currentPlant.namePlant)
            putInt(CURRENT_LEVEL_KEY + currentLocation.nameLocationId, currentPlant.currentLevel)
            putInt(INDICATORr_GROW_KEY + currentLocation.nameLocationId, currentPlant.indicatorGrow.percent)
            putBoolean(IS_PLANT_WATERED_KEY + currentLocation.nameLocationId, currentPlant.isPlantWatered)
            commit()
        }
    }

    private fun setPlaceForCook(currentLocation: Location) {
        val currentPlaceForCook = currentLocation.build!!.placeForCook!!
        with(currentGamePlayProgress.edit()) {
            for (index in currentPlaceForCook.indices) {
                val nameItem = NAME_PLACE_FOR_COOK_KEY + index + currentLocation.nameLocationId
                val sizeItem = SIZE_PLACE_FOR_COOK_KEY + index + currentLocation.nameLocationId

                if (currentPlaceForCook[index] != null) {
                    putInt(nameItem, currentPlaceForCook[index]!!.item.nameItem)
                    putInt(sizeItem, currentPlaceForCook[index]!!.count)
                }
            }
            putInt(COOKING_PROGRESS_KEY + currentLocation.nameLocationId, currentLocation.build!!.cooking!!.percent)
            commit()
        }
    }

    private fun getWater(currentLocation: Location) {
        with(currentGamePlayProgress) {
            val sizeItemKey = SIZE_PLACE_FOR_WATER_KEY + currentLocation.nameLocationId
            val progressWater = EXTRACTING_WATER_PROGRESS_KEY + currentLocation.nameLocationId

            val sizeItem = getInt(sizeItemKey, 0)

            if (sizeItem != 0) {
                currentLocation.build!!.placeForWater = ItemsSlot(Item.FLASK_WITH_WATER, sizeItem)
            }
            currentLocation.build!!.extractingWater!!.percent = getInt(progressWater, 0)
        }
    }

    private fun setWater(currentLocation: Location) {
        with(currentGamePlayProgress.edit()) {
            if (currentLocation.build!!.placeForWater?.count != 0) {
                putInt(SIZE_PLACE_FOR_WATER_KEY + currentLocation.nameLocationId, currentLocation.build!!.placeForWater!!.count)
            }
            putInt(EXTRACTING_WATER_PROGRESS_KEY + currentLocation.nameLocationId, currentLocation.build!!.extractingWater!!.percent)
            commit()
        }
    }

    private fun getAntiThief(currentLocation: Location) {
        with(currentGamePlayProgress) {
            val currentPlaceForAntiThief = currentLocation.build!!.placeForAntiThief
            for (index in currentPlaceForAntiThief.indices) {
                val sizeItemKey = SIZE_ANTI_THIEF_KEY + index + currentLocation.nameLocationId

                val sizeItem = getInt(sizeItemKey, 0)

                if (sizeItem != 0 && index == 0) {
                    currentPlaceForAntiThief[index] = ItemsSlot(Item.PHOSPHORUS_BRIGHT, sizeItem)
                } else if (sizeItem != 0 && index == 1) {
                    currentPlaceForAntiThief[index] = ItemsSlot(Item.PHOSPHORUS_DIM, sizeItem)
                }
            }
        }
    }

    private fun setAntiThief(currentLocation: Location) {
        with(currentGamePlayProgress.edit()) {
            val currentPlaceAntiThief = currentLocation.build!!.placeForAntiThief
            for (index in currentPlaceAntiThief.indices) {
                if (currentPlaceAntiThief[index] != null) {
                    putInt(SIZE_ANTI_THIEF_KEY + index + currentLocation.nameLocationId, currentPlaceAntiThief[index]!!.count)
                }
            }
            putInt(ANTI_THIEF_PROGRESS_KEY + currentLocation.nameLocationId, currentLocation.build!!.antiThief!!.percent)
            commit()
        }
    }

    private fun getFruit(currentLocation: Location) {
        with(currentGamePlayProgress) {
            val nameItemKey = NAME_PLACE_FOR_FRUIT_KEY + currentLocation.nameLocationId
            val sizeItemKey = SIZE_PLACE_FOR_FRUIT_KEY + currentLocation.nameLocationId

            val nameItem = getInt(nameItemKey, 0)

            if (nameItem != 0) {
                loop@for (currentItem in Item.entries) {
                    if (currentItem.nameItem == nameItem) {
                        currentLocation.build!!.placeForFruit = ItemsSlot(currentItem, getInt(sizeItemKey, 0))
                        break@loop
                    }
                }
            }
        }
    }


    fun getGameTimeProgress(): GameTime {
        val progressGameTime = GameTime()
        with(currentGamePlayProgress) {
            progressGameTime.gameTimeValue = getLong(GAME_TIME_VALUE_KEY, 0)
            progressGameTime.gameDaysLeft = getInt(GAME_DAYS_LEFT_KEY, 0)
            progressGameTime.gameSaveFlag = getBoolean(GAME_SAVE_FLAG_KEY, false)
        }
        return progressGameTime
    }

    private fun getNewsList(sizeNewsList: Int): MutableList<News> {
        val resultNewsList = mutableListOf<News>()
        var index = 0

        if (sizeNewsList == 0) return resultNewsList

        while (sizeNewsList != index) {
            val captionNewsKey = CAPTION_NEWS_KEY + "$index"
            val mainTextKey = MAIN_TEXT_NEWS_KEY + "$index"
            val imageId = IMAGE_NEWS_ID_KEY + "$index"

            val news = News()

            with(currentGamePlayProgress) {
                news.captionNews = getInt(captionNewsKey, 0)
                news.mainText = getString(mainTextKey, "").toString()
                news.imageId = getInt(imageId, 0)
            }
            resultNewsList.add(news)
            index++
        }

        return resultNewsList
    }

    private fun setNewsList(newNewsList: List<News>) {
        with(currentGamePlayProgress.edit()) {
            putInt(NEWS_SIZE_KEY, newNewsList.size)
            for (index in newNewsList.indices) {
                val captionNewsKey = CAPTION_NEWS_KEY + "$index"
                val mainTextNewsKey = MAIN_TEXT_NEWS_KEY + "$index"
                val imageNewsIdKey = IMAGE_NEWS_ID_KEY + "$index"

                putInt(captionNewsKey, newNewsList[index].captionNews)
                putString(mainTextNewsKey, newNewsList[index].mainText)
                putInt(imageNewsIdKey, newNewsList[index].imageId)
            }
            commit()
        }

    }

    private fun getBackpackList(sizeSlot: Int): MutableList<ItemsSlot> {
        val resultBackpack = mutableListOf<ItemsSlot>()
        var index = 0
        if (sizeSlot == 0) return resultBackpack

        while (sizeSlot != index) {
            val nameItemKey = NAME_ITEM_KEY + "$index"
            val sizeItemKey = SIZE_ITEM_KEY + "$index"

            val nameItem = currentGamePlayProgress.getInt(nameItemKey, 0)

            val allBuilds = arrayOf(Bonfire(), Well(), Hut(), WoodHouse(), StoneHouse())
            val allItemsInGame = arrayListOf<Slot>()
            allItemsInGame.addAll(Item.entries)
            allItemsInGame.addAll(Armor.entries)
            allItemsInGame.addAll(Weapon.entries)
            allItemsInGame.addAll(allBuilds)

            var itemSlot: ItemsSlot

            loop@for (index in allItemsInGame.indices) {
                if (allItemsInGame[index].nameItem == nameItem) {
                    itemSlot = ItemsSlot(allItemsInGame[index], currentGamePlayProgress.getInt(sizeItemKey, 0))
                    resultBackpack.add(itemSlot)
                    break@loop
                }
            }

            index++
        }

        return resultBackpack
    }

    private fun setBackpackList(newBackpackList: List<ItemsSlot>) {
        with(currentGamePlayProgress.edit()) {
            putInt(BACKPACK_SIZE_KEY, newBackpackList.size)
            for (index in newBackpackList.indices) {
                val nameItemKey = NAME_ITEM_KEY + "$index"
                val sizeItemKey = SIZE_ITEM_KEY + "$index"

                putInt(nameItemKey, newBackpackList[index].item.nameItem)
                putInt(sizeItemKey, newBackpackList[index].count)
            }
            commit()
        }

    }

    private fun getPlacePutOnList(): MutableList<ItemsSlot?> {
        val resultPlacePutOn = mutableListOf<ItemsSlot?>(null, null, null, null)
        var index = 0

        while (index != 4) {
            val namePlaceForPutOnKey = NAME_PLACE_FOR_PUT_ON_KEY + "$index"
            val sizePlaceForPutOnKey = SIZE_PLACE_FOR_PUT_ON_KEY + "$index"

            val namePlaceForPutOn = currentGamePlayProgress.getInt(namePlaceForPutOnKey, 0)

            if (namePlaceForPutOn != 0) {
                when (index) {
                    0 -> {
                        Weapon.values().forEach {
                            if (it.nameItem == namePlaceForPutOn) {
                                resultPlacePutOn[0] = ItemsSlot(it, 1)
                            }
                        }
                    }

                    1 -> {
                        Weapon.values().forEach {
                            if (it.nameItem == namePlaceForPutOn) {
                                resultPlacePutOn[1] = ItemsSlot(
                                    it,
                                    currentGamePlayProgress.getInt(sizePlaceForPutOnKey, 1)
                                )
                            }
                        }
                    }

                    2 -> {
                        Armor.values().forEach {
                            if (it.nameItem == namePlaceForPutOn) {
                                resultPlacePutOn[2] = ItemsSlot(it, 1)
                            }
                        }
                    }

                    3 -> {
                        Item.values().forEach {
                            if (it.nameItem == namePlaceForPutOn) {
                                resultPlacePutOn[3] = ItemsSlot(
                                    it,
                                    currentGamePlayProgress.getInt(sizePlaceForPutOnKey, 1)
                                )
                            }
                        }
                    }
                }
            } else {
                resultPlacePutOn[index] = null
            }

            index++
        }

        return resultPlacePutOn
    }

    private fun setPlacePutOnList(newPlacePutOn: List<ItemsSlot?>) {
        with(currentGamePlayProgress.edit()) {
            for (index in newPlacePutOn.indices) {
                val namePlaceForPutOnKey = NAME_PLACE_FOR_PUT_ON_KEY + "$index"
                val sizePlaceForPutOnKey = SIZE_PLACE_FOR_PUT_ON_KEY + "$index"
                if (newPlacePutOn[index] != null) {
                    putInt(namePlaceForPutOnKey, newPlacePutOn[index]!!.item.nameItem)
                    putInt(sizePlaceForPutOnKey, newPlacePutOn[index]!!.count)
                }
            }
            commit()
        }
    }

    private fun getEffectHaveDeny(sizeEffects: Int): MutableList<Effect> {
        val resultEffectHaveDeny = mutableListOf<Effect>()
        var index = 0

        if (sizeEffects == 0) return resultEffectHaveDeny

        while (sizeEffects != index) {
            val nameEffectHaveDEnyKey = NAME_EFFECT_HAVE_DENY_KEY + "$index"
            val timeEffectHaveDenyKey = TIME_EFFECT_HAVE_DENY_KEY + "$index"
            val nameEffect = currentGamePlayProgress.getInt(nameEffectHaveDEnyKey, 0)

            Effect.values().forEach {
                if (it.nameText == nameEffect) {
                    it.timeAction = currentGamePlayProgress.getLong(timeEffectHaveDenyKey, 0)
                    resultEffectHaveDeny.add(it)
                }
            }
            index++
        }

        return resultEffectHaveDeny
    }

    private fun setEffectHaveDeny(effectHaveDeny: List<Effect>) {
        with(currentGamePlayProgress.edit()) {
            putInt(EFFECT_HAVE_DENY_SIZE_KEY, effectHaveDeny.size)
            for (index in effectHaveDeny.indices) {
                val nameEffectHaveDEnyKey = NAME_EFFECT_HAVE_DENY_KEY + "$index"
                val timeEffectHaveDenyKey = TIME_EFFECT_HAVE_DENY_KEY + "$index"

                putInt(nameEffectHaveDEnyKey, effectHaveDeny[index].nameText)
                putLong(timeEffectHaveDenyKey, effectHaveDeny[index].timeAction)
            }
            commit()
        }
    }

    companion object {
        @JvmStatic
        val GAME_PLAY_PROGRESS_FILE = "game_play_progress_file"

        // player
        @JvmStatic
        val NAME_PLAYER_KEY = "name_player_key"

        @JvmStatic
        val IMAGE_PLAYER_KEY = "image_player_key"

        @JvmStatic
        val HEALTH_VALUE_KEY = "health_value_key"

        @JvmStatic
        val HEALTH_MAX_KEY = "health_max_key"

        @JvmStatic
        val ENDURANCE_VALUE_KEY = "endurance_value_key"

        @JvmStatic
        val ENDURANCE_MAX_KEY = "endurance_max_key"

        @JvmStatic
        val DAMAGE_VALUE_KEY = "damage_value_key"

        @JvmStatic
        val SATIETY_VALUE_KEY = "satiety_value_key"

        @JvmStatic
        val THIRST_VALUE_KEY = "thirst_value_key"

        // newsList
        @JvmStatic
        val NEWS_SIZE_KEY = "news_size_key"

        @JvmStatic
        val CAPTION_NEWS_KEY = "caption_news_key"

        @JvmStatic
        val MAIN_TEXT_NEWS_KEY = "main_text_news_key"

        @JvmStatic
        val IMAGE_NEWS_ID_KEY = "image_news_id_key"

        // Player continue
        @JvmStatic
        val SATIETY_AND_THIRST_SETTING_FLAG_KEY = "satiety_and_thirst_flag_key"

        @JvmStatic
        val HINTS_FLAG_IN_GAME_PLAY_KEY = "hints_flag_in_game_play_key"

        @JvmStatic
        val HINTS_FLAG_IN_FIGHT_KEY = "hint_flag_in_fight_key"

        // backpack
        @JvmStatic
        val BACKPACK_SIZE_KEY = "backpack_size_key"

        @JvmStatic
        val NAME_ITEM_KEY = "name_item_key"

        @JvmStatic
        val SIZE_ITEM_KEY = "size_item_key"

        // placeForPutOn
        @JvmStatic
        val NAME_PLACE_FOR_PUT_ON_KEY = "name_place_for_put_on_key"

        @JvmStatic
        val SIZE_PLACE_FOR_PUT_ON_KEY = "size_place_for_put_on_key"

        // effectHaveDeny
        @JvmStatic
        val EFFECT_HAVE_DENY_SIZE_KEY = "effect_have_deny_size_key"

        @JvmStatic
        val NAME_EFFECT_HAVE_DENY_KEY = "name_effect_have_deny_key"

        @JvmStatic
        val TIME_EFFECT_HAVE_DENY_KEY = "size_effect_have_deny_key"

        // Player continue
        @JvmStatic
        val LEVEL_KEY = "level_key"

        @JvmStatic
        val EXP_KEY = "exp_key"

        @JvmStatic
        val STUDY_POINTS_KEY = "study_points_key"

        // GameTime
        @JvmStatic
        val GAME_TIME_VALUE_KEY = "game_time_value_key"

        @JvmStatic
        val GAME_DAYS_LEFT_KEY = "game_days_left"

        @JvmStatic
        val GAME_SAVE_FLAG_KEY = "game_save_flag_key"

        // Location
        @JvmStatic
        val IS_PLAYER_FIND_HERE_KEY = "is_player_find_here_key"

        @JvmStatic
        val NAME_BUILD_KEY = "name_build_key"

        //BUILD
        @JvmStatic
        val NAME_PLACE_FOR_COOK_KEY = "name_place_for_cook_key"

        @JvmStatic
        val SIZE_PLACE_FOR_COOK_KEY = "size_place_for_cook_key"

        @JvmStatic
        val COOKING_PROGRESS_KEY = "cooking_progress_key"

        @JvmStatic
        val SIZE_PLACE_FOR_WATER_KEY = "size_place_for_water_key"

        @JvmStatic
        val EXTRACTING_WATER_PROGRESS_KEY = "extracting_water_progress_key"

        @JvmStatic
        val SIZE_PLACE_FOR_ANTI_THIEF = "size_place_for_anti_thief"

        @JvmStatic
        val ANTI_THIEF_PROGRESS_KEY = "anti_thief_progress_key"

        @JvmStatic
        val SIZE_ANTI_THIEF_KEY = "size_anti_thief_key"

        @JvmStatic
        val NAME_PLACE_FOR_FRUIT_KEY = "name_place_for_fruit_key"

        @JvmStatic
        val SIZE_PLACE_FOR_FRUIT_KEY = "size_place_for_fruit_key"

        // Plant
        @JvmStatic
        val NAME_PLANT_KEY = "name_plant_key"

        @JvmStatic
        val CURRENT_LEVEL_KEY = "current_level_key"

        @JvmStatic
        val INDICATORr_GROW_KEY = "indicator_grow_key"

        @JvmStatic
        val IS_PLANT_WATERED_KEY = "is_plant_watered"


    }

}