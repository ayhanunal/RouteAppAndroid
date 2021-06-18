package com.ayhanunal.routeapp.fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.util.SIGN_IN_SP
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.sign_in_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class SigninFragment : Fragment(R.layout.sign_in_fragment) {

    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPreferences = requireContext().getSharedPreferences(SIGN_IN_SP, 0)
        val signedRoomID = sharedPreferences.getString("signed_room_id", "")
        val signedRoomDate = sharedPreferences.getString("signed_room_date", "")
        val signedRoomMsg = sharedPreferences.getString("signed_room_msg", "")
        if (!signedRoomID.isNullOrEmpty()){
            findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToPlacesFragment(signedRoomID, signedRoomDate!!, signedRoomMsg!!))
        }

        db = FirebaseFirestore.getInstance()

        signin_room_signup_text_button.setOnClickListener {
            //create room
            showSignUpPopup()
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

                                sharedPreferences.edit().putString("signed_room_id", documentID).apply()
                                sharedPreferences.edit().putString("signed_room_date", roomDate).apply()
                                sharedPreferences.edit().putString("signed_room_msg", roomMsg).apply()

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

    fun showSignUpPopup(){
        val adb = AlertDialog.Builder(requireContext())
        val adbInflater = LayoutInflater.from(requireContext())
        val customPopup = adbInflater.inflate(R.layout.sign_up_popup, null)

        adb.setView(customPopup)
        adb.setCancelable(false)
        Handler(Looper.getMainLooper()).post{
            val dialog = adb.create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val roomName = customPopup.findViewById<EditText>(R.id.sign_up_popup_room_name_text)
            val roomPass = customPopup.findViewById<EditText>(R.id.sign_up_popup_room_pass_text)
            val roomMsg = customPopup.findViewById<EditText>(R.id.sign_up_popup_msg_text)

            val createButton = customPopup.findViewById<Button>(R.id.sign_up_popup_create_button)
            createButton.setOnClickListener {
                //create room
                if (roomName.text.toString().isNotEmpty() && roomPass.text.toString().isNotEmpty()){
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())

                    val postData = hashMapOf(
                        "message" to roomMsg.text.toString(),
                        "name" to roomName.text.toString(),
                        "pass" to roomPass.text.toString(),
                        "saveDate" to currentDate
                    )

                    db.collection("Room")
                        .add(postData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Welcome Room", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToPlacesFragment(roomName.text.toString(), currentDate, roomMsg.text.toString()))
                            dialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error, ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }

                }else{
                    Toast.makeText(requireContext(), "Room Name/Pass is not empty!!", Toast.LENGTH_SHORT).show()
                }

            }
            customPopup.findViewById<ImageView>(R.id.sign_up_popup_close_button).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

}