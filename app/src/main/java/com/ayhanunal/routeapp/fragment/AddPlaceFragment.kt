package com.ayhanunal.routeapp.fragment

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.listener.OnPlacesDetailsListener
import `in`.madapps.placesautocomplete.model.Place
import `in`.madapps.placesautocomplete.model.PlaceDetails
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_place.*
import java.util.*

class AddPlaceFragment : Fragment(R.layout.fragment_add_place) {

    private var selectedLatitude: String? = null
    private var selectedLongitude: String? = null
    private val db = Firebase.firestore

    private lateinit var roomID: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomID = arguments?.getString("room_id") ?: ""

        add_places_back_icon.setOnClickListener {
            findNavController().popBackStack()
        }


        val placeApi = PlaceAPI.Builder().apiKey(resources.getString(R.string.api_key)).build(requireContext())
        add_place_auto_complete_edit_text.setAdapter(PlacesAutoCompleteAdapter(requireContext(), placeApi))

        add_place_auto_complete_edit_text.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position) as Place
            add_place_auto_complete_edit_text.setText(place.description)

            placeApi.fetchPlaceDetails(place.id, object : OnPlacesDetailsListener{
                override fun onError(errorMessage: String) {
                    showPopup()
                }

                override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
                    selectedLatitude = placeDetails.lat.toString()
                    selectedLongitude = placeDetails.lng.toString()

                }
            })
        }

        add_place_save_button.setOnClickListener {
            if (selectedLatitude != null && selectedLongitude != null){
                if (add_place_name_text.text.toString().isNotEmpty()){

                    val locationName = add_place_name_text.text.toString()
                    val locationDescription = add_place_desc_text.text.toString()
                    val priority = add_place_range_slider.value

                    //save firebase
                    val postData = hashMapOf(
                        "UUID" to UUID.randomUUID().toString(),
                        "name" to locationName,
                        "description" to locationDescription,
                        "isActive" to true,
                        "latitude" to selectedLatitude,
                        "longitude" to selectedLongitude,
                        "priority" to priority,
                        "time" to System.currentTimeMillis(),
                        "savedPhone" to android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
                    )
                    db.collection("Room")
                        .document(roomID)
                        .collection("Locations")
                        .add(postData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Success, location saved", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error, ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }


                }else{
                    Toast.makeText(requireContext(), "Error, location name cannot be empty !!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Error, location cannot be empty !!", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun showPopup(){
        val adb = AlertDialog.Builder(requireContext())
        val adbInflater = LayoutInflater.from(requireContext())
        val customPopup = adbInflater.inflate(R.layout.custom_place_add_popup, null)

        adb.setView(customPopup)
        adb.setCancelable(false)
        Handler(Looper.getMainLooper()).post{
            val dialog = adb.create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val latEditText = customPopup.findViewById<EditText>(R.id.add_place_popup_lat_text)
            val lngEditText = customPopup.findViewById<EditText>(R.id.add_place_popup_lng_text)

            val setButton = customPopup.findViewById<Button>(R.id.add_place_popup_set_button)
            setButton.setOnClickListener {
                //set lat-lng

                if (latEditText.text.toString().isNotEmpty() && lngEditText.text.toString().isNotEmpty()){
                    selectedLatitude = latEditText.text.toString()
                    selectedLongitude = lngEditText.text.toString()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), "Please check the fields !!", Toast.LENGTH_SHORT).show()
                }

            }

            customPopup.findViewById<ImageView>(R.id.add_place_popup_close_button).setOnClickListener {
                dialog.dismiss()
            }
        }

    }

}