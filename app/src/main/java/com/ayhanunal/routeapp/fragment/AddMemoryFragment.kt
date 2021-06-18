package com.ayhanunal.routeapp.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_memory.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AddMemoryFragment : Fragment(R.layout.fragment_add_memory) {

    private lateinit var roomID: String
    private lateinit var currentLat: String
    private lateinit var currentLng: String

    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    var selectedImage: Bitmap? = null
    var imageData: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        roomID = arguments?.getString("room_id") ?: ""
        currentLat = arguments?.getString("current_lat") ?: ""
        currentLng = arguments?.getString("current_lng") ?: ""

        add_memory_add_photo_icon.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }
        }

        add_memory_back_icon.setOnClickListener {
            findNavController().popBackStack()
        }

        add_memory_save_button.setOnClickListener {
            uploadAndSave()
        }


    }

    private fun uploadAndSave(){

        val memoryName = add_memory_name_text.text.toString()
        val memoryDesc = add_memory_desc_text.text.toString()
        val memoryPriority = add_memory_range_slider.value

        if (imageData != null){

            if (!memoryName.isNullOrEmpty()){
                var uuid = UUID.randomUUID()
                val imageName = "images/$uuid.jpg"

                storageReference.child(imageName).putFile(imageData!!).addOnSuccessListener {

                    var newRef = FirebaseStorage.getInstance().getReference(imageName)
                    newRef.downloadUrl.addOnSuccessListener {

                        val downloadUrl = it.toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())

                        val postData = hashMapOf(
                            "memName" to memoryName,
                            "memDescription" to memoryDesc,
                            "memIsActive" to true,
                            "memLatitude" to currentLat,
                            "memLongitude" to currentLng,
                            "memPriority" to memoryPriority,
                            "memTime" to System.currentTimeMillis(),
                            "memDate" to currentDate,
                            "memImageUrl" to downloadUrl,
                            "memSavedPhone" to android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
                        )

                        db.collection("Room")
                            .document(roomID)
                            .collection("Memories")
                            .add(postData)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Success, memory saved", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error, ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }


                    }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "name is not empty!!", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(requireContext(), "Please, select a picture!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageData = data.data
            if (imageData != null){
                try {
                    if (Build.VERSION.SDK_INT >= 28){
                        var source = ImageDecoder.createSource(requireActivity().contentResolver, imageData!!)
                        selectedImage = ImageDecoder.decodeBitmap(source)
                        add_memory_image_view.setImageBitmap(selectedImage)
                        add_memory_image_linear_layout.visibility = View.VISIBLE
                    }else{
                        selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageData)
                        add_memory_image_view.setImageBitmap(selectedImage)
                        add_memory_image_linear_layout.visibility = View.VISIBLE
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }



}