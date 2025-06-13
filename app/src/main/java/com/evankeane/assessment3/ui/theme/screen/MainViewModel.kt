package com.evankeane.assessment3.ui.theme.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evankeane.assessment3.model.Mobil
import com.evankeane.assessment3.network.ApiStatus
import com.evankeane.assessment3.network.MobilApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Mobil>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

//    fun retrieveData(userId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            status.value = ApiStatus.LOADING
//            try {
//                data.value = MobilApi.service.getMobil(userId)
//                status.value = ApiStatus.SUCCESS
//            } catch (e: Exception) {
//                Log.d("MainViewModel", "Failure: ${e.message}")
//                status.value = ApiStatus.FAILED
//            }
//        }
//    }
//    fun retrieveAllData(userId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            status.value = ApiStatus.LOADING
//            try {
//                val result = MobilApi.service.getMobil(userId)
//                data.value = result
//                status.value = ApiStatus.SUCCESS
//            } catch (e: Exception) {
//                Log.d("MainViewModel", "Failure: ${e.message}")
//                status.value = ApiStatus.FAILED
//            }
//        }
//    }

    fun retrieveData(userId: String = "all") {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val result = MobilApi.service.getMobil(userId)
                data.value = result
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }


    fun saveData(userId: String, namaMobil: String, hargaMobil: String,tahun:String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.postMobil(
                    userId,
                    namaMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    hargaMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tahun.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultiPartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }

    }

    fun updateData(userId: String, namaMobil: String, hargaMobil: String,tahun:String, bitmap: Bitmap, id:String) {
        Log.d("DEBUG", "User : $userId , namaMobil : $namaMobil, harga : $hargaMobil, Tahun : $tahun , Bitmap : $bitmap , id : $id")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.updateMobil(
                    userId,
                    namaMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    hargaMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tahun.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultiPartBody(),
                    id
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }

    }

    fun deleteData(userId: String, mobilId: String) {
        Log.d("DEBUG", "delete Data: UserId= $userId, mobilId= $mobilId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.deleteMobil(
                    userId = userId,
                    id = mobilId
                )

                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Error delete: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultiPartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "gambar", "image.jpg", requestBody)
    }

    fun clearMessage() { errorMessage.value = null }


}
