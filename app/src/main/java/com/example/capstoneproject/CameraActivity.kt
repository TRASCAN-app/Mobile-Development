package com.example.capstoneproject


import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Size
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider

import androidx.core.content.ContextCompat
import com.example.capstoneproject.ApiPredict.ResponsePredict
import com.example.capstoneproject.ApiPredict.ResponsePredictError
import com.example.capstoneproject.Retrofit.ApiConfig
import com.example.capstoneproject.Retrofit.ApiService
import com.example.capstoneproject.databinding.ActivityCameraBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import android.view.View

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var currentImageUri: Uri? = null
    private var loadingDialog: LoadingDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Executor CameraX
        cameraExecutor = Executors.newSingleThreadExecutor()

        //Kamera
        startCamera()

        // Switch kamera
        binding.btnSwitchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        // Ambil Gambar
        binding.btnCameraX.setOnClickListener {
            takePhoto()
        }

        // Gallery
        binding.buttonImageX.setOnClickListener {
            openGallery()
        }
        binding.btnClose.setOnClickListener {
            finish()
        }

    }
    private fun reduceImageSize(uri: Uri): File {
        val maxFileSize = 1024 * 1024 // 1MB
        var quality = 100 // Mulai dengan kualitas maksimum
        val contentResolver = applicationContext.contentResolver

        // Dapatkan InputStream dari URI
        val inputStream = contentResolver.openInputStream(uri) ?: throw FileNotFoundException("Unable to open InputStream for URI: $uri")
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Kurangi ukuran gambar
        var stream: ByteArrayOutputStream
        do {
            stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            quality -= 5 // Kurangi kualitas setiap iterasi
        } while (stream.size() > maxFileSize && quality > 0)

        // Tulis gambar yang dikompres ke file sementara
        val tempFile = File.createTempFile("reduced_image", ".jpg", cacheDir)
        FileOutputStream(tempFile).use {
            it.write(stream.toByteArray())
            it.flush()
        }

        return tempFile
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Konfigurasi preview untuk menampilkan tampilan kamera
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Konfigurasi ImageCapture dengan resolusi lebih rendah
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720)) // Resolusi rendah (HD) untuk mengurangi ukuran file
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // Optimalkan untuk kecepatan

                .build()

            try {
                // Pastikan semua use cases dilepas sebelum bind
                cameraProvider.unbindAll()

                // Bind preview dan ImageCapture ke lifecycle
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector, // Pilih kamera belakang
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed: ${e.message}", e)
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Format nama file
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/CameraApp") // Menyimpan ke folder DCIM
        }

        // MediaStore URI
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    if (savedUri != null) {
                        Log.d("CameraX", "Photo capture succeeded: $savedUri")
                        Toast.makeText(this@CameraActivity, "Photo saved to Gallery: $savedUri", Toast.LENGTH_SHORT).show()
                        sendImageToApi(savedUri)
                    }

                }
            }
        )
    }
// Mengirim Image Ke API
    private fun sendImageToApi(imageUri: Uri) {
    showLoadingDialog()
        try {
            // Ambil ukuran file dari URI
            val fileSize = getFileSizeFromUri(imageUri)
            val maxSize = 1024 * 1024 // 1MB

            // Kurangi ukuran file
            val finalFile = if (fileSize > maxSize) {
                reduceImageSize(imageUri) // Reduksi jika ukurannya lebih besar
            } else {
                File(getPathFromUri(imageUri) ?: throw IllegalStateException("Cannot resolve file path"))
            }
            // Buat MultipartBody
            val requestFile = finalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageBody = MultipartBody.Part.createFormData("file", finalFile.name, requestFile)


            // Kirim ke API
            val apiService = ApiConfig.getApiService()

            apiService.uploadImage(imageBody).enqueue(object : Callback<ResponsePredict> {
                override fun onResponse(call: Call<ResponsePredict>, response: Response<ResponsePredict>) {
                    hideLoadingDialog()
                    if (response.isSuccessful) {
                        val predictResult = response.body()
                        if (predictResult != null) {
                            val result = predictResult.result ?: "Gambar Tidak bisa di deteksi"
                            val score = predictResult.score?.toString() ?: "0.0"
                            val recyclable = predictResult.recyclable ?: false
                            val imageUrl = predictResult.imageUrl ?: ""
                            val wasteType = predictResult.wasteType ?: "Unknown"

                            // Kirim data ke ResultActivity
                            val intent = Intent(this@CameraActivity, ResultActivity::class.java).apply {
                                putExtra("result", result)
                                putExtra("score", score)
                                putExtra("recyclable", recyclable)
                                putExtra("image_url", imageUrl)
                                putExtra("waste_type", wasteType)
                            }
                            startActivity(intent)
                        } else{
                            Toast.makeText(this@CameraActivity, "Unexpected empty response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CameraActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponsePredict>, t: Throwable) {
                    hideLoadingDialog()
                    t.printStackTrace()
                    Toast.makeText(this@CameraActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            hideLoadingDialog()
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Image Error", "Error: ${e.message}")
        }
    }

//Mengirim Image Ke API dari Gallery
    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("Photo Picker", "Selected media URI: $uri")
            currentImageUri = uri

            sendImageToApi(uri) // Kirim gambar yang dipilih ke API
        } else {
            Log.d("Photo Picker", "No media selected")
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
    private fun getFileSizeFromUri(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val fileSize = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
            cursor.close()
            fileSize
        } else {
            0L
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
        }
        loadingDialog?.show(supportFragmentManager, "LoadingDialog")
    }
    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }



}
