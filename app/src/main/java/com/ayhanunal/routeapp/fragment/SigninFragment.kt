package com.ayhanunal.routeapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ayhanunal.routeapp.R
import kotlinx.android.synthetic.main.sign_in_fragment.*

class SigninFragment : Fragment(R.layout.sign_in_fragment) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        signin_room_signup_text_button.setOnClickListener {
            //create room
        }

        signin_room_signin_button.setOnClickListener {
            //sign in room
        }

    }

}