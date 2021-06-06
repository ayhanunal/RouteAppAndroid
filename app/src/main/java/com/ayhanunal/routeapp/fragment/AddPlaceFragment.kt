package com.ayhanunal.routeapp.fragment

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.listener.OnPlacesDetailsListener
import `in`.madapps.placesautocomplete.model.Place
import `in`.madapps.placesautocomplete.model.PlaceDetails
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.ayhanunal.routeapp.R
import kotlinx.android.synthetic.main.fragment_add_place.*

class AddPlaceFragment : Fragment(R.layout.fragment_add_place) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val placeApi = PlaceAPI.Builder().apiKey(resources.getString(R.string.api_key)).build(requireContext())
        autoCompleteEditText.setAdapter(PlacesAutoCompleteAdapter(requireContext(), placeApi))

        autoCompleteEditText.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position) as Place
            autoCompleteEditText.setText(place.description)

            placeApi.fetchPlaceDetails(place.id, object : OnPlacesDetailsListener{
                override fun onError(errorMessage: String) {
                    Log.e("AAA error", errorMessage)
                }

                override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
                    Log.e("AAA lat", placeDetails.lat.toString())
                    Log.e("AAA lng", placeDetails.lng.toString())
                    Log.e("AAA address", placeDetails.address.toString())
                    Log.e("AAA vicinity", placeDetails.vicinity.toString())
                }
            })
        }

    }

}