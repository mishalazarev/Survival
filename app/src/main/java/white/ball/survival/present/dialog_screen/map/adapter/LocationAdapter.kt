package white.ball.survival.present.dialog_screen.map.adapter

import android.view.LayoutInflater
import android.view.View
import white.ball.survival.domain.model.location.Location
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.databinding.TravelLocaionBinding
import white.ball.survival.domain.navigator.NavigatorByMap

class LocationAdapter(
    private val navigator: NavigatorByMap
) : RecyclerView.Adapter<LocationAdapter.LocationHolder>(), View.OnClickListener {

    var locationList: List<Location> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class LocationHolder(val binding: TravelLocaionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TravelLocaionBinding.inflate(layoutInflater, parent, false)

        binding.let {
            it.iconLocationImageView.setOnClickListener(this)
            it.nameLocationTextView.setOnClickListener(this)
            it.travelLocationRadioButton.setOnClickListener(this)
            it.wrapperTravelLocation.setOnClickListener(this)
        }

        return LocationHolder(binding)
    }

    override fun getItemCount(): Int = locationList.size

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        val location = locationList[position]

        holder.binding.run {
            iconLocationImageView.setImageResource(location.imageIconTravelResId)
            iconLocationImageView.tag = location
            nameLocationTextView.setText(location.nameLocationId)
            nameLocationTextView.tag = location
            travelLocationRadioButton.isChecked = location.isPlayerFindHere
            travelLocationRadioButton.tag = location
            wrapperTravelLocation.isActivated = location.isPlayerFindHere
            wrapperTravelLocation.tag = location
        }

    }

    override fun onClick(view: View) {
        val location = view.tag as Location

        when(view.id) {
            else -> navigator.onClickedLocationPressed(location)
        }
    }

}