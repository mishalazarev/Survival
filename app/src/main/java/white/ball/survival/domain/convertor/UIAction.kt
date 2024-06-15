package white.ball.survival.domain.convertor

import android.content.Context

interface UIRepository{

    fun getString(resId: Int): String

    fun getIconForItemResImage(index: Int): Int

    fun getContext(): Context
}