package com.mobile.cw2

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mobile.cw2.AccessPermission

class SplashActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // requestPermission()
        proceedToHomeScreen()
    }

    private fun proceedToHomeScreen() {
        Log.d("123","asd")
        // Delay the transition to simulate a splash screen
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }

    private fun requestPermission() {
        Log.d("2234","asd")
        // Delay the transition to simulate a splash screen
        Handler().postDelayed({
            val intent = Intent(this, AccessPermission::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }


}