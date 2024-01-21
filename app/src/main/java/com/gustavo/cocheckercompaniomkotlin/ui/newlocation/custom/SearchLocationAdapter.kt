package com.gustavo.cocheckercompaniomkotlin.ui.newlocation.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.R
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SearchLocationAdapter (val mPlacesClient: PlacesClient,val onClick: (AutocompletePrediction)-> Unit) : RecyclerView.Adapter<SearchLocationAdapter.ViewHolder>(), Filterable {

    private val token = AutocompleteSessionToken.newInstance()
    private var mSearchResults: ArrayList<AutocompletePrediction> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchLocationAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_places_search_result, parent, false))
    }

    override fun onBindViewHolder(holder: SearchLocationAdapter.ViewHolder, position: Int) {
        val prediction:AutocompletePrediction = mSearchResults.get(position)
        holder.onBind(prediction)
        holder.itemView.setOnClickListener {
            onClick(prediction)
        }
    }

    override fun getItemCount(): Int = mSearchResults.size

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = mSearchResults
                results.count = mSearchResults.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            }

        }
    }

    suspend fun filterWithPredictions(query: String){
        val querySearchResult = getPredictions(query)
        mSearchResults.clear()
        mSearchResults.addAll(querySearchResult)
        filter.filter(query)
        notifyDataSetChanged()
    }

    private suspend fun getPredictions(query: String): List<AutocompletePrediction> = suspendCoroutine { continuation ->
        mPlacesClient.findAutocompletePredictions(
            FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()
        ).addOnSuccessListener { response : FindAutocompletePredictionsResponse->
            continuation.resume(response.autocompletePredictions)
        }.addOnFailureListener{
            continuation.resumeWithException(it)
            LoggerUtil.printStackTraceOnlyInDebug(it)
        }
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        fun onBind(prediction: AutocompletePrediction){
            itemView.apply {
                findViewById<TextView>(R.id.item_search_title)
                .text = prediction.getPrimaryText(null)
                findViewById<TextView>(R.id.item_search_subtitle).text = prediction.getSecondaryText(null)
            }
        }
    }

}