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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ayhanunal.routeapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_memory.*
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