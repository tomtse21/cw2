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
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.mobile.cw2.databinding.FragAddpageBinding
import java.io.File
import java.util.concurrent.ExecutorService


class AddPage : Fragment() {
    private var _binding: FragAddpageBinding? = null
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognitionBtn: MaterialButton
    private lateinit var voiceBtn: MaterialButton
    private lateinit var saveBtn : Button
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


    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragAddpageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputImageBtn = view.findViewById(R.id.inputImageBtn)
        recognitionBtn = view.findViewById(R.id.recognizeTextBtn)
        voiceBtn = view.findViewById(R.id.voiceBtn)
        saveBtn = view.findViewById(R.id.saveBtn)

        imageIv = view.findViewById(R.id.imageIv)
        questionEt = view.findViewById(R.id.question)
        answerEt = view.findViewById(R.id.answer)

        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storageaPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textRecognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        inputImageBtn.setOnClickListener{
            showInputImageDialog()
        }

        saveBtn.setOnClickListener{
            save()
        }

        recognitionBtn.setOnClickListener{
            if(imageUri == null){
                showToast("Pick Image Frist,.,,")
            }else{
                recognizeTextFromImage()
            }
        }


    }

    private fun save() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun recognizeTextFromImage() {
        progressDialog.setMessage("Preparing image")
        progressDialog.show()

        try {
            val inputImage = InputImage.fromFilePath(requireActivity(), imageUri!!)
            progressDialog.setMessage("Recognizing text...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text->
                    progressDialog.dismiss()
                    val recognizedText = text.text

//                    recognizedTextEt.setText(recognizedText)
                    val recognizedTextAry = recognizedText.split("\n").toTypedArray();
//

                    questionEt.setText(recognizedTextAry[0])
                    answerEt.setText(recognizedTextAry[1])
//                    val myListAdapter = MyListAdapter(this,recognizedText.split("\n").toTypedArray(),)
//                    listView.adapter = myListAdapter
//
//                    listView.setOnItemClickListener(){adapterView, view, position, id ->
//                        val itemAtPos = adapterView.getItemAtPosition(position)
//                        val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//                        Toast.makeText(this, "Click on item at $itemAtPos its item id $itemIdAtPos", Toast.LENGTH_LONG).show()
//                    }
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
        val popMenu = PopupMenu(activity,inputImageBtn)
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
                recognizeTextFromImage()
            }else{
                showToast("Canceled")
            }


        }

    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Sample  Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Title");
        val resolver = requireActivity().contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

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
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return cameraResult && storageResult
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(requireActivity(),storageaPermissions,
            AddPage.STORAGE_REQUEST_CODE
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(),cameraPermissions,
            AddPage.CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            AddPage.CAMERA_REQUEST_CODE ->{
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

            AddPage.STORAGE_REQUEST_CODE ->{
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    //it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(ContentValues.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


}