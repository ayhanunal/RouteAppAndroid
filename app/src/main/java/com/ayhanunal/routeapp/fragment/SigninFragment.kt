package com.ayhanunal.routeapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.sign_in_fragment.*

class SigninFragment : Fragment(R.layout.sign_in_fragment) {

    private lateinit var db: FirebaseFirestore
    private val SIGN_IN_SP = "signInRoom"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()

        signin_room_signup_text_button.setOnClickListener {
            //create room
            Toast.makeText(requireContext(), "Currently Unavailable, Under Development", Toast.LENGTH_SHORT).show()
        }

        signin_room_signin_button.setOnClickListener {
            //sign in room
            val roomName = signin_room_name_edit_text.text.toString()
            val roomPass = signin_room_pass_edit_text.text.toString()


            db.collection("Room")
                .whereEqualTo("name", roomName)
                .whereEqualTo("pass", roomPass)
                .addSnapshotListener { snapshot, error ->
                    if (error != null){
                        Toast.makeText(requireContext(), "Error, ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }else{
                        if (snapshot != null){
                            if (!snapshot.isEmpty){
                                val document = snapshot.documents[0]
                                val documentID = document.id
                                val roomDate = document.get("saveDate") as String
                                val roomMsg = document.get("message") as String
                                findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToPlacesFragment(documentID, roomDate, roomMsg))

                            }else{
                                Toast.makeText(requireContext(), "Invalid Room Name or Pass!!", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireContext(), "Invalid Room Name or Pass!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

        }

    }

}