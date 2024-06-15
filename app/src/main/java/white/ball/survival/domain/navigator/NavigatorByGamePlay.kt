package white.ball.survival.domain.navigator

import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

typealias ResultListener<T> = (T) -> Unit

fun Fragment.navigator(): NavigatorByGamePlay {
    return requireActivity() as NavigatorByGamePlay
}

interface NavigatorByGamePlay {

    fun onLaunchDialogFragmentPressed(fragment: DialogFragment)
}