package com.mobile.cw2.uitl

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mobile.cw2.R
import com.mobile.cw2.TextReg

class AccessPermission: AppCompatActivity() {

    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storageaPermissions: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storageaPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestStoragePermission();
        requestCameraPermission();

        Log.i("Accesss Status : Storage",requestStoragePermission().toString())
        Log.i("Accesss Status : Camera",requestCameraPermission().toString())

    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storageaPermissions,
            AccessPermission.STORAGE_REQUEST_CODE
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,
            AccessPermission.CAMERA_REQUEST_CODE
        )
    }



}