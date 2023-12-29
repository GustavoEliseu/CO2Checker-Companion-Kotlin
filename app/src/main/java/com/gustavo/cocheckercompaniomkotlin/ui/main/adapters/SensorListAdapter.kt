package com.gustavo.cocheckercompaniomkotlin.ui.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.SensorItemViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.getSafeMapUrlString
import com.gustavo.cocheckercompaniomkotlin.utils.load
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.SensorItemBinding

class SensorListAdapter(private val viewMoreClick: (String) -> Unit,
private val currentSensors: MutableList<SensorItemList> = mutableListOf<SensorItemList>()
) : RecyclerView.Adapter<SensorListAdapter.SensorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        return SensorViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.sensor_item,
                parent,
                false
            ), viewMoreClick
        )
    }

    fun setCurrentSensors(newSensors: List<SensorItemList>) {
        currentSensors.clear()
        currentSensors.addAll(newSensors)
        notifyDataSetChanged()
    }

    fun addSensor(sensorWifiData: SensorItemList) {
        currentSensors.add(0, sensorWifiData)
        notifyItemInserted(0)
    }

    fun removeSensor(sensorWifiData: SensorItemList){
        safeRun {
            val index = currentSensors.indexOf(sensorWifiData)
            currentSensors.removeAt(index)
        }
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val sensorItem = currentSensors[position]
        holder.bind(sensorItem, position)
    }

    fun clear() {
        currentSensors.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = currentSensors.size

    abstract class SensorListViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class SensorViewHolder(
        private val binding: SensorItemBinding,
        val clickListener: (String) -> Unit
    ) : SensorListViewHolder(binding.root) {

        private val viewModel = SensorItemViewModel()

        @SuppressLint("ClickableViewAccessibility")
        fun bind(sensorItem: SensorItemList, position: Int) {
            binding.viewModel = viewModel

            binding.root.setOnClickListener {
                clickListener(sensorItem.mac)
            }

            val latitude = sensorItem.LastLocation?.locationLatitude
            val longitude = sensorItem.LastLocation?.locationLongitude
            val imageUrl = getSafeMapUrlString(
                binding.root.context,
                latitude,
                longitude
            )
            binding.sensorImage.load(imageUrl, isRound = true)

            viewModel.bind(sensorItem)

        }
    }
}