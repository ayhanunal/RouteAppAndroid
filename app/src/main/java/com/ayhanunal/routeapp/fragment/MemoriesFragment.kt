package com.ayhanunal.routeapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.adapter.LocationsAdapter
import com.ayhanunal.routeapp.adapter.MemoriesAdapter
import com.ayhanunal.routeapp.model.Memories
import com.ayhanunal.routeapp.util.SwipeHelper
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

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(memories_recycler_view){
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                val markAsUnreadButton = activateChangesButton(position)
                val archiveButton = directionsButton(position)
                buttons = listOf(deleteButton, markAsUnreadButton, archiveButton)
                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(memories_recycler_view)



    }

    fun deleteFromFirestore(position: Int){
        val documentID = memoriesArray[position].memDocumentID
        db.collection("Room")
            .document(roomID)
            .collection("Memories")
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
        val documentID = memoriesArray[position].memDocumentID
        val status = memoriesArray[position].memIsActive
        db.collection("Room")
            .document(roomID)
            .collection("Memories")
            .document(documentID)
            .update("memIsActive", !status)
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
                    val url = "http://maps.google.com/maps?daddr=" + memoriesArray[position].memLatitude + "," + memoriesArray[position].memLongitude
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            })
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