package com.example.voteapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}
