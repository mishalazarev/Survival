package white.ball.survival.present.dialog_screen.backpack.progress_bar

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet


class ProgressBar : androidx.appcompat.widget.AppCompatTextView {

    private var maxValue = 100

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :  super(context!!, attrs, defStyle)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    constructor(context: Context?) : super(context!!)

    fun setMaxValue(newMaxValue: Int) {
        maxValue = newMaxValue
    }

    @Synchronized
    fun setValue(value: Int) {
        this.text = "$value / $maxValue"
        val checkValue = if (value == 0) {
            1
        } else {
            value
        }
        val checkMaxValue = if (maxValue == 0) {
            1
        } else {
            maxValue
        }


        val background = background as LayerDrawable
        val barValue = background.getDrawable(1) as ClipDrawable
        val newClipLevel = checkValue * 10000 / checkMaxValue
        barValue.level = newClipLevel

        drawableStateChanged()
    }

}