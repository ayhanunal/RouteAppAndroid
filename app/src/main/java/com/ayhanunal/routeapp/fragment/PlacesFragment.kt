package com.ayhanunal.routeapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment : Fragment(R.layout.fragment_places) {

    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        places_add_place_icon.setOnClickListener {
            findNavController().navigate(PlacesFragmentDirections.actionPlacesFragmentToAddPlaceFragment())
        }

        //Log.e("AAAA Status", "On Create")

    }

    fun getLocationsFromFirestore(){
        db.collection("Locations")
            .get()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }



}