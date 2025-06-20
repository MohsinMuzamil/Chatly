package com.mohsin.chatly.util

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

object CloudinaryUploader {
    fun uploadImage(
        context: Context,
        imageUri: Uri,
        cloudName: String,
        unsignedPreset: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            onFailure(IOException("Cannot open input stream for given Uri"))
            return
        }

        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        try {
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", unsignedPreset)
                .build()

            val url = "https://api.cloudinary.com/v1_1/db1dwaldu/image/upload"
            val request = Request.Builder().url(url).post(requestBody).build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    tempFile.delete()
                    onFailure(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    tempFile.delete()
                    if (!response.isSuccessful) {
                        val errorMsg = response.body?.string()
                        onFailure(IOException("Upload failed with code ${response.code}: $errorMsg"))
                        return
                    }
                    val body = response.body?.string()
                    try {
                        val json = JSONObject(body ?: "{}")
                        val url = json.getString("secure_url")
                        onSuccess(url)
                    } catch (e: Exception) {
                        onFailure(e)
                    }
                }
            })
        } catch (e: Exception) {
            tempFile.delete()
            onFailure(e)
        }
    }
}