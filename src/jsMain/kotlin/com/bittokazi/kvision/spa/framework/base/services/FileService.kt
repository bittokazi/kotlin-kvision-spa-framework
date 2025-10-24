package com.bittokazi.kvision.spa.framework.base.services

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.models.FileResponse
import com.bittokazi.kvision.spa.framework.base.models.SpaResult
import io.kvision.form.upload.UploadInput
import io.kvision.jquery.jQuery
import kotlinx.serialization.json.Json
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.xhr.FormData
import kotlin.js.json

class FileService {

    fun upload(
        fileName: String,
        uploadInput: UploadInput,
        retry: Boolean = true,
        url: String = "",
        fn: (SpaResult<FileResponse, FileServiceError>) -> Unit
    ) {
        val formData = FormData()

        uploadInput.value?.forEach { file ->
            console.log(uploadInput.getNativeFile(file)!!.size)
            formData.set("file", uploadInput.getNativeFile(file)!!)
            formData.set(
                "uploadObject",
                Blob(
                    blobParts = arrayOf(
                        Json.encodeToString(
                            mapOf(
                                "filename" to fileName,
                                "absoluteFilePath" to ""
                            )
                        )
                    ),
                    BlobPropertyBag("application/json")
                )
            )
        }

        val token = SpaAppEngine.defaultAuthHolder.getAuth()?.token ?: run { return@run "" }

        jQuery.ajaxSettings.contentType = false
        jQuery.ajaxSettings.processData = false
        jQuery.ajaxSettings.method = "POST"
        jQuery.ajaxSettings.url = url
        jQuery.ajaxSettings.data = formData
        jQuery.ajaxSettings.headers = json(
            Pair("Authorization", "Bearer $token")
        )

        jQuery.post(url, formData).then(
            { result: dynamic, textStatus: String, jqXHR: dynamic ->
                console.log("POST request successful")
                console.log("Result: $result")
                console.log("Status: $textStatus")
                console.log("XHR: $jqXHR")

                // Handle the response
                if (result != null) {
                    val jsonString = JSON.stringify(result)
                    val fileResponse = Json.decodeFromString<FileResponse>(jsonString)
                    fn(SpaResult.Success(fileResponse))
                }
            },
            { jqXHR, textStatus, errorThrown ->
                console.error("POST request failed")
                console.error("XHR: $jqXHR")
                console.error("Status: $textStatus")
                console.error("Error: $errorThrown")

                val httpStatusCode = jqXHR.status.toInt() // Get the HTTP status code

                console.error("HTTP Status Code: $httpStatusCode") // Log the status code

                if (httpStatusCode == 401) {
                    if (retry) {
                        SpaAppEngine.restService.refreshTokenAndRetry<Any>(null, null, null) {
                            if(it) {
                                upload(
                                    fileName = fileName,
                                    uploadInput = uploadInput,
                                    fn = fn,
                                    retry = false
                                )
                            } else {
                                fn(SpaResult.Failure(FileServiceError.SERVER_ERROR))
                            }
                        }
                    } else {
                        SpaAppEngine.spaAuthService.logout()
                    }
                } else {
                    val errorMessage = if (jqXHR.responseJSON != null) {
                        JSON.stringify(jqXHR.responseJSON)
                    } else {
                        errorThrown
                    }

                    fn(SpaResult.Failure(FileServiceError.SERVER_ERROR))
                }
            }
        )
    }
}

enum class FileServiceError {
    SERVER_ERROR;
}
