package com.ayhanunal.routeapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.adapter.LocationsAdapter
import com.ayhanunal.routeapp.model.Locations
import com.ayhanunal.routeapp.util.LAST_LOCATION_SP
import com.ayhanunal.routeapp.util.SwipeHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_places.*
import java.lang.Exception


class PlacesFragment : Fragment(R.layout.fragment_places) {

    private lateinit var db: FirebaseFirestore
    private val locationsArray = ArrayList<Locations>()
    private var recyclerViewAdapter: LocationsAdapter? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    private lateinit var roomID: String
    private lateinit var roomDate: String
    private lateinit var roomMsg: String

    private var totalCost = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        places_log_out_icon.setOnClickListener {
            val loginSp = requireContext().getSharedPreferences("signInRoom", 0)
            loginSp.edit().clear().apply()
            findNavController().navigateUp()
        }

        places_memories_icon.setOnClickListener {
            findNavController().navigate(PlacesFragmentDirections.actionPlacesFragmentToMemoriesFragment(roomID, currentLat.toString(), currentLng.toString()))
        }

        roomID = arguments?.getString("room_id") ?: ""
        roomDate = arguments?.getString("room_date") ?: ""
        roomMsg = arguments?.getString("room_msg") ?: ""

        val sharedPreferences = requireContext().getSharedPreferences(LAST_LOCATION_SP, 0)
        currentLat = sharedPreferences!!.getFloat("lastKnowLat", 39.7098351.toFloat()).toDouble()
        currentLng = sharedPreferences!!.getFloat("lastKnowLng", 31.2269539.toFloat()).toDouble()

        swipe_refresh_layout.setOnRefreshListener {
            obtieneLocalizacion()
            swipe_refresh_layout.isRefreshing = false
        }

        places_add_place_icon.setOnClickListener {
            findNavController().navigate(PlacesFragmentDirections.actionPlacesFragmentToAddPlaceFragment(roomID))
        }

        db = FirebaseFirestore.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestPermission()
        getDataFromFirestore()


        //Recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        places_recycler_view.layoutManager = layoutManager
        recyclerViewAdapter = LocationsAdapter(locationsArray)
        places_recycler_view.adapter = recyclerViewAdapter


        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(places_recycler_view){
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                val markAsUnreadButton = activateChangesButton(position)
                val archiveButton = directionsButton(position)
                buttons = listOf(deleteButton, markAsUnreadButton, archiveButton)
                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(places_recycler_view)

        places_info_icon.setOnClickListener {
            showInfoPopup()
        }


    }

    fun showInfoPopup(){
        val adb = AlertDialog.Builder(requireContext())
        val adbInflater = LayoutInflater.from(requireContext())
        val customPopup = adbInflater.inflate(R.layout.info_popup, null)

        adb.setView(customPopup)
        adb.setCancelable(false)
        Handler(Looper.getMainLooper()).post{
            val dialog = adb.create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            customPopup.findViewById<TextView>(R.id.info_popup_room_name_text).text = "Room Name: HIDDEN"
            customPopup.findViewById<TextView>(R.id.info_popup_cost_text).text = "Total Cost: ${totalCost.toString()} TL"
            customPopup.findViewById<TextView>(R.id.info_popup_info_text).text = roomMsg.toString()
            customPopup.findViewById<TextView>(R.id.info_popup_room_date_text).text = "Room Date: ${roomDate.toString()}"

            val okButton = customPopup.findViewById<Button>(R.id.info_popup_ok_button)
            okButton.setOnClickListener {
                dialog.dismiss()
            }

        }
    }

    fun deleteFromFirestore(position: Int){
        val documentID = locationsArray[position].documentID
        db.collection("Room")
            .document(roomID)
            .collection("Locations")
            .document(documentID)
            .delete()
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                getDataFromFirestore()
            }
    }

    fun activateChanges(position: Int){
        val documentID = locationsArray[position].documentID
        val status = locationsArray[position].isActive
        db.collection("Room")
            .document(roomID)
            .collection("Locations")
            .document(documentID)
            .update("isActive", !status)
            .addOnSuccessListener {
                getDataFromFirestore()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    deleteFromFirestore(position)
                }
            })
    }

    private fun activateChangesButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Deactivate",
            14.0f,
            android.R.color.holo_green_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    activateChanges(position)
                }
            })
    }

    private fun directionsButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Directions",
            14.0f,
            android.R.color.holo_blue_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val url = "http://maps.google.com/maps?daddr=" + locationsArray[position].latitude + "," + locationsArray[position].longitude
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion(){
        //buraya sonra bak gec guncelliyor.
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLat =  location?.latitude
                currentLng = location?.longitude

                Log.e("AAAA", "lat: ${currentLat} lng: ${currentLng}")

                if (currentLat != null && currentLng != null){
                    getDataFromFirestore()
                    val sharedPreferences = requireContext().getSharedPreferences(LAST_LOCATION_SP, 0)
                    sharedPreferences.edit().putFloat("lastKnowLat", currentLat!!.toFloat()).apply()
                    sharedPreferences.edit().putFloat("lastKnowLng", currentLng!!.toFloat()).apply()
                }else{
                    //bu kismi duzelt , boyle olursa latlng bulamayinca default deger alicak.
                    currentLat = 39.7098351
                    currentLng = 31.2269539
                    getDataFromFirestore()
                }
            }

    }


    fun getDataFromFirestore(){
        Log.e("AAA lat getD", currentLat.toString())
        Log.e("AAA lng getD", currentLng.toString())
        db.collection("Room")
            .document(roomID)
            .collection("Locations")
            .addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(requireContext(), "Error, ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        locationsArray.clear()
                        totalCost = 0
                        for (document in snapshot.documents){
                            val takenUuid = document.get("UUID") as String
                            val takenName = document.get("name") as String
                            val takenDesc = document.get("description") as String
                            val takenLat = document.get("latitude") as String
                            val takenLng = document.get("longitude") as String
                            val takenIsActive = document.get("isActive") as Boolean
                            var takenPriority = 1.0
                            try {
                                takenPriority = (document.get("priority") as Long).toDouble()
                            }catch (e:Exception){
                                takenPriority = document.get("priority") as Double
                            }
                            val takenTime = document.get("time") as Long
                            val takenSavedPhone = document.get("savedPhone") as String
                            val takenAddress = document.get("address") as String
                            val distance = getDistance(currentLat!!.toDouble(), currentLng!!.toDouble(), takenLat.toDouble(), takenLng.toDouble())
                            val takenPrice = document.get("price") as String

                            try {
                                totalCost += takenPrice.toInt()
                            }catch (e: Exception){
                                e.printStackTrace()
                            }

                            val locations = Locations(takenUuid, takenName, takenDesc, takenLat, takenLng, takenIsActive, takenPriority.toInt(), takenSavedPhone, takenTime.toInt(), distance, 25, document.id, takenAddress, takenPrice)
                            locationsArray.add(locations)
                        }

                        locationsArray.sortBy ({selector(it)})
                        recyclerViewAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun selector(l: Locations): Float = l.distance

    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val distance = FloatArray(2)
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance)
        return distance[0]
    }

    fun requestPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }else{
            obtieneLocalizacion()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                obtieneLocalizacion()
            }
            else{
                Toast.makeText(requireContext(), "you need to give permission for the app to run!!", Toast.LENGTH_SHORT).show()
                requestPermission()
            }
        }
    }


}