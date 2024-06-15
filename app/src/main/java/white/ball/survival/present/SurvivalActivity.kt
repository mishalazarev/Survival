package white.ball.survival.present

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import white.ball.survival.databinding.ActivitySurvivalBinding
import white.ball.survival.domain.navigator.NavigatorByGamePlay

class SurvivalActivity : AppCompatActivity(), NavigatorByGamePlay {

    private lateinit var binding: ActivitySurvivalBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySurvivalBinding.inflate(layoutInflater).also { setContentView(it.root) }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

    }

    override fun onLaunchDialogFragmentPressed(dialogFragment: DialogFragment) {
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }
}