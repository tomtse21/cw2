package com.mobile.cw2

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.lang.Exception

class TextReg: AppCompatActivity() {
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognitionBtn: MaterialButton
    private lateinit var voiceBtn: MaterialButton
    private lateinit var imageIv: ImageView
    private lateinit var recognizedTextEt: EditText
    private lateinit var listView: ListView
    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }

    private var imageUri: Uri? = null
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storageaPermissions: Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer: TextRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text_reg)


        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognitionBtn = findViewById(R.id.recognizeTextBtn)
        voiceBtn = findViewById(R.id.voiceBtn)

        imageIv = findViewById(R.id.imageIv)
       // recognizedTextEt = findViewById(R.id.recognizedTextEt)
        listView = findViewById(R.id.listView)

        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storageaPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textRecognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        inputImageBtn.setOnClickListener{
            showInputImageDialog()
        }

        recognitionBtn.setOnClickListener{
            if(imageUri == null){
                showToast("Pick Image Frist,.,,")
            }else{
                recognizeTextFromImage()
            }
        }
    }

    fun speak(view: View){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Start Speaking")
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        recognizedTextEt.setText(
//            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString())
    }


    private fun recognizeTextFromImage() {
        progressDialog.setMessage("Preparing image")
        progressDialog.show()

        try {
            val inputImage = InputImage.fromFilePath(this, imageUri!!)
            progressDialog.setMessage("Recognizing text...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text->
                    progressDialog.dismiss()
                    val recognizedText = text.text

//                    recognizedTextEt.setText(recognizedText)

                    val myListAdapter = MyListAdapter(this,recognizedText.split("\n").toTypedArray(),)
                    listView.adapter = myListAdapter

                    listView.setOnItemClickListener(){adapterView, view, position, id ->
                        val itemAtPos = adapterView.getItemAtPosition(position)
                        val itemIdAtPos = adapterView.getItemIdAtPosition(position)
                        Toast.makeText(this, "Click on item at $itemAtPos its item id $itemIdAtPos", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    showToast("Failed to prepare image due to ${e.message}")
                }

        }catch (e: Exception){
            progressDialog.dismiss()
            showToast("Failed to prepare image due to ${e.message}")
        }
    }

    private fun showInputImageDialog() {
        val popMenu = PopupMenu(this,inputImageBtn)
        popMenu.menu.add(Menu.NONE, 1, 1, "CAMERA")
        popMenu.menu.add(Menu.NONE, 2, 2, "GALLERY")

        popMenu.show()

        popMenu.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId

            if(id == 1){
                if(checkCameraPermission()){
                    pickImageCamera()
                }else{
//                    pickImageGallery()
                   requestCameraPermission()
                }
            }else if(id == 2){
                if(checkStoragePermission()){
                    pickImageGallery()
                }else{
                    requestStoragePermission()
                }
            }
            return@setOnMenuItemClickListener  true

        }
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        galleryActivityResultLauncher.launch(intent)

    }

    private val  galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data
                imageIv.setImageURI(imageUri)
            }else{
                showToast("Canceled")
            }


        }

    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Sample  Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Title");

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

            if(result.resultCode == Activity.RESULT_OK){
                imageIv.setImageURI(imageUri)
            }else{
                showToast("Canceled")
            }


        }
    private fun checkStoragePermission(): Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return cameraResult && storageResult
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storageaPermissions,
            STORAGE_REQUEST_CODE
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,
            CAMERA_REQUEST_CODE
        )
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
                        pickImageCamera()
                    }else{
                        showToast("Camera & Storage permission are required...")
                    }
                }
            }

            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(storageAccepted){
                        pickImageGallery()
                    }else{
                        showToast("Storage permission are required...")
                    }
                }
            }


        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}