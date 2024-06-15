package white.ball.survival.present.view_model_factory

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import white.ball.survival.domain.app.MainApp

typealias ViewModelCreator = (MainApp) -> ViewModel?

class SurvivalViewModelFactory(
    private val mainApp: MainApp,
    private val viewModelCreator: ViewModelCreator = { null }
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelCreator(mainApp) as T
    }
}

inline fun <reified VM: ViewModel> Fragment.viewModelCreator(noinline creator: ViewModelCreator): Lazy<VM> {
    return viewModels{ SurvivalViewModelFactory(requireContext().applicationContext as MainApp,creator) }
}