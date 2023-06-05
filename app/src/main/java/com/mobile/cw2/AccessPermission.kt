package com.mobile.cw2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mobile.cw2.R
import com.mobile.cw2.TextReg

class AccessPermission: AppCompatActivity() {

    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Accesss Status : Storage",requestStoragePermission().toString())
        Log.i("Accesss Status : Camera",requestCameraPermission().toString())
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestStoragePermission();
        requestCameraPermission();



    }

    private fun requestStoragePermission(){
        Log.i("1234","asd")
        ActivityCompat.requestPermissions(this,storagePermissions,
            STORAGE_REQUEST_CODE
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        recognizedTextEt.setText(
//            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            CAMERA_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){

                        Log.e("eeeeeeee","eeeeee")
                        //  startCamera()
                    }else{

                    }
                }
            }

            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(storageAccepted){

                    }else{

                    }
                }
            }


        }
    }


}