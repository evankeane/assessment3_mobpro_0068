package com.evankeane.assessment3.network


import com.evankeane.assessment3.model.Mobil
import com.evankeane.assessment3.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
//import retrofit2.http.Query

private const val BASE_URL = "https://grouper-superb-polecat.ngrok-free.app/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MobilApiService {
    @GET("kendaraans")
    suspend fun getMobil(
        @Header("Authorization") userId: String
    ): List<Mobil>


    @Multipart
    @POST("kendaraans/store")
    suspend fun postMobil(
        @Header("Authorization") userId: String,
        @Part("namaMobil") nama: RequestBody,
        @Part("hargaMobil") harga: RequestBody,
        @Part("tahun") tahun: RequestBody,
        @Part gambar: MultipartBody.Part
    ): OpStatus

    @DELETE("kendaraans/{id}")
    suspend fun deleteMobil(
        @Header("Authorization") userId: String,
        @Path("id") id: String
    ):OpStatus

    @Multipart
    @POST("/kendaraans/{id}")
    suspend fun updateMobil(
        @Header("Authorization") userId: String,
        @Part("namaMobil") nama: RequestBody,
        @Part("hargaMobil") harga: RequestBody,
        @Part("tahun") tahun: RequestBody,
        @Part gambar: MultipartBody.Part,
        @Path ("id") id : String
    ): OpStatus

}

object MobilApi {
    val service: MobilApiService by lazy {
        retrofit.create(MobilApiService::class.java)
    }

    fun getMobilUrl(imageId: String): String {
        return "https://grouper-superb-polecat.ngrok-free.app/storage/$imageId"
    }
}
enum class ApiStatus { LOADING, SUCCESS, FAILED }