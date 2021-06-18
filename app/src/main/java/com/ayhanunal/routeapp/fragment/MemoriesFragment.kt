package com.ayhanunal.routeapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.adapter.LocationsAdapter
import com.ayhanunal.routeapp.adapter.MemoriesAdapter
import com.ayhanunal.routeapp.model.Memories
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_memories.*
import kotlinx.android.synthetic.main.fragment_places.*

class MemoriesFragment : Fragment (R.layout.fragment_memories){

    private lateinit var roomID: String
    private lateinit var currentLat: String
    private lateinit var currentLng: String

    private lateinit var db: FirebaseFirestore
    private val memoriesArray = ArrayList<Memories>()
    private var recyclerViewAdapter: MemoriesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memories_back_icon.setOnClickListener {
            findNavController().popBackStack()
        }

        memories_add_memory_icon.setOnClickListener {
            findNavController().navigate(MemoriesFragmentDirections.actionMemoriesFragmentToAddMemoryFragment(roomID, currentLat, currentLng))
        }

        roomID = arguments?.getString("room_id") ?: ""
        currentLat = arguments?.getString("current_lat") ?: ""
        currentLng = arguments?.getString("current_lng") ?: ""


        memories_swipe_refresh_layout.setOnRefreshListener {
            getDataFromFirestore()
            memories_swipe_refresh_layout.isRefreshing = false
        }

        //Recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        memories_recycler_view.layoutManager = layoutManager
        recyclerViewAdapter = MemoriesAdapter(memoriesArray)
        memories_recycler_view.adapter = recyclerViewAdapter

        db = FirebaseFirestore.getInstance()
        getDataFromFirestore()



    }

    fun getDataFromFirestore(){
        db.collection("Room")
            .document(roomID)
            .collection("Memories")
            .orderBy("memTime")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    Toast.makeText(requireContext(), "Error, ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }else{
                    if (snapshot != null){
                        if (!snapshot.isEmpty){
                            memoriesArray.clear()
                            for (document in snapshot.documents){
                                val takenName = document.get("memName") as String
                                val takenDesc = document.get("memDescription") as String
                                val takenLat = document.get("memLatitude") as String
                                val takenLng = document.get("memLongitude") as String
                                val takenIsActive = document.get("memIsActive") as Boolean
                                var takenPriority = 1.0
                                try{
                                    takenPriority = (document.get("memPriority") as Long).toDouble()
                                }catch (e: Exception){
                                    takenPriority = document.get("memPriority") as Double
                                }
                                val takenDate = document.get("memDate") as String
                                val takenSavedPhone = document.get("memSavedPhone") as String
                                val takenImageUrl = document.get("memImageUrl") as String

                                val memories = Memories(takenName, takenDesc, takenLat, takenLng, takenIsActive, takenPriority.toInt(), takenSavedPhone, takenDate, document.id, takenImageUrl)
                                memoriesArray.add(memories)
                            }

                            recyclerViewAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
    }



}