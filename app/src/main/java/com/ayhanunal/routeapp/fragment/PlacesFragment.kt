package com.ayhanunal.routeapp.fragment

import android.icu.text.SymbolTable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.adapter.LocationsAdapter
import com.ayhanunal.routeapp.model.Locations
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.coroutines.*

class PlacesFragment : Fragment(R.layout.fragment_places) {

    private lateinit var db: FirebaseFirestore
    private val locationsArray = ArrayList<Locations>()
    private var recyclerViewAdapter: LocationsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        places_add_place_icon.setOnClickListener {
            findNavController().navigate(PlacesFragmentDirections.actionPlacesFragmentToAddPlaceFragment())
        }

        db = FirebaseFirestore.getInstance()
        getDataFromFirestore()

        //Recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        places_recycler_view.layoutManager = layoutManager
        recyclerViewAdapter = LocationsAdapter(locationsArray)
        places_recycler_view.adapter = recyclerViewAdapter



    }


    fun getDataFromFirestore(){
        db.collection("Locations").addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(requireContext(), "Error, ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        for (document in snapshot.documents){
                            val takenUuid = document.get("UUID") as String
                            val takenName = document.get("name") as String
                            val takenDesc = document.get("description") as String
                            val takenLat = document.get("latitude") as String
                            val takenLng = document.get("longitude") as String
                            val takenIsActive = document.get("isActive") as Boolean
                            val takenPriority = document.get("priority") as Double
                            val takenTime = document.get("time") as Long
                            val takenSavedPhone = document.get("savedPhone") as String

                            val locations = Locations(takenUuid, takenName, takenDesc, takenLat, takenLng, takenIsActive, takenPriority.toInt(), takenSavedPhone, takenTime.toInt())
                            locationsArray.add(locations)

                            recyclerViewAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }



}