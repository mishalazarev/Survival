package white.ball.survival.present.dialog_screen.map

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import white.ball.survival.R
import white.ball.survival.databinding.FragmentMapDialogBinding
import white.ball.survival.domain.model.location.Location
import white.ball.survival.domain.navigator.NavigatorByMap
import white.ball.survival.domain.navigator.navigator
import white.ball.survival.present.dialog_screen.map.adapter.LocationAdapter
import white.ball.survival.present.dialog_screen.map.view_model.MapViewModel
import white.ball.survival.present.view_model_factory.viewModelCreator

class MapDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentMapDialogBinding
    private lateinit var adapter: LocationAdapter

    private val viewModel: MapViewModel by viewModelCreator {
        MapViewModel(
            it.playerAction,
            it.interactionWithEnvironment
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val view = View.inflate(activity, R.layout.fragment_map_dialog, null)
            val dialog = AlertDialog.Builder(it)
            binding = FragmentMapDialogBinding.bind(view)
            val linearLayoutManager = LinearLayoutManager(context)

            viewModel.reloadMap()

            adapter = LocationAdapter(object : NavigatorByMap{
                override fun onClickedLocationPressed(location: Location) {
                    viewModel.onClickedLocationPressed(location)
                    adapter.notifyDataSetChanged()
                }
            })

            with(binding) {
                exitTextView.setOnClickListener {
                    dismiss()
                }
                locationRecyclerView.adapter = adapter
                locationRecyclerView.layoutManager = linearLayoutManager

                travelButton.setOnClickListener {
                    onTravelingLocationPressed()
                    dismiss()
                }
            }

            adapter.locationList = Location.values().toList()

            dialog
                .setView(view)
                .create()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onTravelingLocationPressed() {
        if (viewModel.player.value!!.endurance.indicator.percent >= COUNT_ENDURANCE_FOR_TRAVEL && !viewModel.travelInLocation()) {
            Toast.makeText(requireContext(),R.string.already_fiend_in_this_place, Toast.LENGTH_SHORT).show()
        } else if (!viewModel.travelInLocation() && viewModel.player.value!!.endurance.indicator.percent < COUNT_ENDURANCE_FOR_TRAVEL) {
            Toast.makeText(requireContext(),R.string.count_endurance_not_enough, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        val window: Window = dialog!!.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    companion object {

        @JvmStatic
        val COUNT_ENDURANCE_FOR_TRAVEL = 25
    }
}