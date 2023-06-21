package com.mobile.cw2

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.mobile.cw2.uitl.AppDatabase
import com.mobile.cw2.uitl.QA
import com.mobile.cw2.uitl.QADao
import java.io.File
import java.util.concurrent.ExecutorService

typealias LumaListener = (luma: Double) -> Unit
class TextReg: AppCompatActivity() {
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognitionBtn: MaterialButton
    private lateinit var voiceBtn1: MaterialButton
    private lateinit var voiceBtn2: MaterialButton
    private lateinit var imageIv: ImageView
    private lateinit var recognizedTextEt: EditText
    private lateinit var listView: ListView
    private lateinit var questionEt : EditText
    private lateinit var answerEt : EditText
    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }

    private var imageUri: Uri? = null
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storageaPermissions: Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer: TextRecognizer

    private var mTopToolbar: Toolbar? = null

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    lateinit var qaDao: QADao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text_reg)

        val actionBar: ActionBar? = supportActionBar

        mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognitionBtn = findViewById(R.id.recognizeTextBtn)
        voiceBtn1 = findViewById(R.id.voiceBtn1)
        voiceBtn2 = findViewById(R.id.voiceBtn2)

        imageIv = findViewById(R.id.imageIv)
        questionEt = findViewById(R.id.question)
        answerEt = findViewById(R.id.answer)


        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storageaPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

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

        val db =  Room.databaseBuilder(applicationContext, AppDatabase::class.java, "qaDB")
            .allowMainThreadQueries().build()
        qaDao = db.qaDao()

        questionEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                answerEt.requestFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        answerEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(answerEt.windowToken, 0)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun save(view: View){
        val score = QA(id = 0, question = questionEt.text.toString(), answer = answerEt.text.toString())

        qaDao.insertAll(score)

        Toast.makeText(applicationContext, "Question saved.", Toast.LENGTH_LONG).show()

    }

    fun swipeText(view: View){
        val orgQ = questionEt.text;
        val orgA = answerEt.text;

        questionEt.text = orgA
        answerEt.text = orgQ
    }

    fun speak1(view: View){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Start Speaking")
        startActivityForResult(intent,101)
    }

    fun speak2(view: View){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Start Speaking")
        startActivityForResult(intent,102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 101){
            questionEt.setText(
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString())
        }else if(requestCode == 102){
            answerEt.setText(
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString())
        }

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

                    val recognizedTextAry = recognizedText.split("\n").toTypedArray();
//

                    questionEt.setText(recognizedTextAry[0])
                    answerEt.setText(recognizedTextAry[1])

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
                recognizeTextFromImage()
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
                recognizeTextFromImage()
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
                        Log.e("eeeeeeee","eeeeee")
                      //  startCamera()
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
                        recognizeTextFromImage()
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