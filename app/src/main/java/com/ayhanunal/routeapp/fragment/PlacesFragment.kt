package com.ayhanunal.routeapp.fragment

import android.icu.text.SymbolTable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.model.Locations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment : Fragment(R.layout.fragment_places) {

    private val db = Firebase.firestore
    private val locationsArray = ArrayList<Locations>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        places_add_place_icon.setOnClickListener {
            findNavController().navigate(PlacesFragmentDirections.actionPlacesFragmentToAddPlaceFragment())
        }

        //Log.e("AAAA Status", "On Create")

        getLocationsFromFirestore()
        Log.e("AAA", locationsArray.toString())

    }

    fun getLocationsFromFirestore(){
        db.collection("Locations")
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    val takenUuid = document.data.get("UUID") as String
                    val takenName = document.data.get("name") as String
                    val takenDesc = document.data.get("description") as String
                    val takenLat = document.data.get("latitude") as String
                    val takenLng = document.data.get("longitude") as String
                    val takenIsActive = document.data.get("isActive") as Boolean
                    val takenPriority = document.data.get("priority") as Double
                    val takenTime = document.data.get("time") as Long
                    val takenSavedPhone = document.data.get("savedPhone") as String

                    locationsArray.add(Locations(takenUuid, takenName, takenDesc, takenLat, takenLng, takenIsActive, takenPriority.toInt(), takenSavedPhone, takenTime.toInt()))
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error, ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }



}