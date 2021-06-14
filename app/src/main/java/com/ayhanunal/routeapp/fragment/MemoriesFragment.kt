package com.ayhanunal.routeapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.adapter.MemoriesAdapter
import com.ayhanunal.routeapp.model.Memories
import com.google.firebase.firestore.FirebaseFirestore

class MemoriesFragment : Fragment (R.layout.fragment_memories){

    private lateinit var roomID: String
    private lateinit var currentLat: String
    private lateinit var currentLng: String

    private lateinit var db: FirebaseFirestore
    private val memoriesArray = ArrayList<Memories>()
    private var recyclerViewAdapter: MemoriesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomID = arguments?.getString("room_id") ?: ""
        currentLat = arguments?.getString("current_lat") ?: ""
        currentLng = arguments?.getString("current_lng") ?: ""






    }

}