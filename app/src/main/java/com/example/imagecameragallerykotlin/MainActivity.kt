package com.example.imagecameragallerykotlin


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.imagecameragallerykotlin.services.ApiService
import com.example.imagecameragallerykotlin.services.ServiceBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView :ImageView
    private lateinit var gallery :Button
    private lateinit var camera :Button
    private lateinit var upload :Button
    private lateinit var imageUri : Uri
    var selectedUri : Uri?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById<ImageView>(R.id.imageView)
        gallery = findViewById<Button>(R.id.gallery)
        camera = findViewById<Button>(R.id.camera)
        upload = findViewById<Button>(R.id.upload)

        //Image From Gallery
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
            if(uri!=null){
                imageView.setImageURI(uri)
                selectedUri = uri
            }
        }

        gallery.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        //Image From Gallery

        //Image From Camera
        imageUri = createImageUri()
        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(imageUri != null){
                selectedUri = imageUri
                imageView.setImageURI(imageUri)
            }
        }
        camera.setOnClickListener {
           takePicture.launch(imageUri)
            true
        }
        //Image From Camera

        upload.setOnClickListener {
            var progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Kotlin Progress Bar")
            progressDialog.setMessage("Application is loading, please wait")
            progressDialog.show()
            Log.d("Upload Message",selectedUri.toString())
            Log.d("Upload Message", selectedUri?.path.toString())

            val filesDir = applicationContext.filesDir
            val file = File(filesDir,"image.png")

            val inputStream = contentResolver.openInputStream(selectedUri!!)
            val outputStream = FileOutputStream(file)
            inputStream!!.copyTo(outputStream)

            val requestBody = RequestBody.create(MediaType.parse("image/*"),file)
            val part = MultipartBody.Part.createFormData("pic",file.name,requestBody)
            lifecycleScope.launch(Dispatchers.IO){
                val apiService: ApiService = ServiceBuilder.buildService(ApiService::class.java)
                val filter = HashMap<String,String>()

                filter["image_name"]="image111"
                val requestCall : Call<JsonObject> = apiService.uploadImage(part,filter)

                requestCall.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(p0: Call<JsonObject>, p1: Response<JsonObject>) {
                        if(p1.isSuccessful){
                            Log.d("api response1", p1.body().toString())
                            progressDialog.dismiss()
                        }
                    }

                    override fun onFailure(p0: Call<JsonObject>, p1: Throwable) {
                        Log.d("api response2", p1.toString())
                    }

                })
            }
        }


    }


    fun createImageUri():Uri{
        val image = File(applicationContext.filesDir,"camera_photo.png")

        return FileProvider.getUriForFile(applicationContext,"com.example.imagecameragallerykotlin.fileprovider",image)
    }


}