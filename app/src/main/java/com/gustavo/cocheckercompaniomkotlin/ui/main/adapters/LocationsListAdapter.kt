package com.gustavo.cocheckercompaniomkotlin.ui.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gustavo.cocheckercompaniomkotlin.base.BaseAdapter
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.LocationItemViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.load
import com.gustavo.cocheckercompaniomkotlin.utils.getSafeMapUrlString
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.LocationItemBinding

class LocationsListAdapter(
    private val viewMoreClick: (String, String) -> Unit,
    private val currentLocations: MutableList<LocationItemList> = mutableListOf<LocationItemList>()
) : RecyclerView.Adapter<LocationsListAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.location_item,
                parent,
                false
            ), viewMoreClick
        )
    }

    fun setCurrentLocations(newLocations: List<LocationItemList>) {
        currentLocations.clear()
        currentLocations.addAll(newLocations)
        notifyDataSetChanged()
    }

    fun addLocation(locationData: LocationItemList) {
        currentLocations.add(0, locationData)
        notifyItemInserted(0)
    }

    fun removeLocation(locationData: LocationItemList) {
        safeRun {
            val index = currentLocations.indexOf(locationData)
            currentLocations.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val sensorItem = currentLocations[position]
        holder.bind(sensorItem, position)
    }

    fun clear() {
        currentLocations.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = currentLocations.size

    class LocationViewHolder(
        private val binding: LocationItemBinding,
        val clickListener: (String, String) -> Unit
    ) : BaseAdapter.BaseViewHolder(binding.root) {

        private val viewModel = LocationItemViewModel()

        @SuppressLint("ClickableViewAccessibility")
        fun bind(locationItemList: LocationItemList, position: Int) {
            binding.viewModel = viewModel

            val imageUrl = getSafeMapUrlString(
                locationItemList.latitude.toString(),
                locationItemList.longitude.toString()
            )

            binding.locationImage.load(imageUrl)

            binding.root.setOnClickListener {
                clickListener(locationItemList.uuid, locationItemList.name)
            }
            viewModel.bind(locationItemList)
        }
    }
}