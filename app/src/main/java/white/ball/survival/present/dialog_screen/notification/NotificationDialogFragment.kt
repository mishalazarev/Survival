package white.ball.survival.present.dialog_screen.notification

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import white.ball.survival.R
import white.ball.survival.databinding.FragmentNofificationDialogBinding
import white.ball.survival.present.dialog_screen.notification.adapter.NotificationAdapter
import white.ball.survival.present.dialog_screen.notification.view_model.NotificationViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator


class NotificationDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentNofificationDialogBinding

    private lateinit var adapter: NotificationAdapter

    private val viewModel: NotificationViewModel by viewModelCreator {
        NotificationViewModel(
            it.playerAction
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context,R.layout.fragment_nofification_dialog,null)
        val dialog = AlertDialog.Builder(activity)
        binding = FragmentNofificationDialogBinding.bind(view)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = NotificationAdapter()
        adapter.newsList = viewModel.getNewsList()
        binding.newsRecyclerView.layoutManager = layoutManager
        binding.newsRecyclerView.adapter = adapter

        if (adapter.newsList.isEmpty()) {
            binding.captionEmptyNewsTextView.visibility = View.VISIBLE
        }

        with(binding) {
            iconClearImageButton.setOnClickListener {
                viewModel.clearNewsList()
                adapter.newsList = viewModel.getNewsList()
                adapter.notifyDataSetChanged()
                captionEmptyNewsTextView.visibility = View.VISIBLE
            }

            exitTextView.setOnClickListener {
                dismiss()
            }
        }

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()

        val window = dialog!!.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}