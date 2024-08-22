package com.example.imagecameragallerykotlin.services

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface ApiService {
    @GET("common/get_products.php")
    fun getData(): Call<JsonObject>

    @POST("common/get_product.php")
    fun getDatas(@QueryMap filter: HashMap<String,String>): Call<JsonObject>

    @Multipart
    @POST("common/upload_image.php")
    fun uploadImage(@Part image : MultipartBody.Part,@QueryMap filter: HashMap<String,String>): Call<JsonObject>
}