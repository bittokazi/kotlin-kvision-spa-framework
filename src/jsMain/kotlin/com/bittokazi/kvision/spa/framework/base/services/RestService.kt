package com.bittokazi.kvision.spa.framework.base.services

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.AuthHolderType
import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine.defaultAuthHolder
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.utils.sweetAlert
import io.kvision.rest.RemoteRequestException
import io.kvision.rest.RestClient
import io.kvision.rest.RestResponse
import io.kvision.rest.post
import io.kvision.state.ObservableValue
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.Promise

class RestService {

    var BASE_URL = window.location.origin
    var REFRESH_TOKEN_ENDPOINT = "${BASE_URL}/oauth2/refresh/token"

    var refreshTokenObservable: ObservableValue<Boolean?> = ObservableValue(null)
    var refreshingToken = false

    fun getClient(): RestClient {
        return RestClient() {
            val headerList = mutableListOf<Pair<String, String>>()

            val token = defaultAuthHolder.getAuth()?.token ?: run { return@run "" }
            headerList.add("Authorization" to "Bearer $token")

            if (!SpaAppEngine.defaultTenantHolder.getTenant().isNullOrEmpty()) {
                headerList.add("X-DATA-TENANT" to SpaAppEngine.defaultTenantHolder.getTenant()!!)
            }
            headers = {
                headerList
            }
        }
    }

    fun getPublicClient(): RestClient {
        return RestClient()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> createAuthCall(req: () -> Promise<RestResponse<T>>): Promise<RestResponse<T>> {
        return Promise { resolve, reject ->
            req().then {
                resolve(it)
            }.catch { throwable ->
                if(throwable is RemoteRequestException) {
                    if (throwable.code.toInt() == 401) {
                        refreshTokenAndRetry(req, resolve, reject)
                    } else if (throwable.code.toInt() == 400) {
                        sweetAlert.fire(
                            Json.encodeToDynamic(
                                mapOf(
                                    "title" to "Error",
                                    "text" to "Invalid input. Please correct them.",
                                    "icon" to "error"
                                )
                            )
                        )
                        reject(throwable)
                    } else if (throwable.code.toInt() == 404) {
                        sweetAlert.fire(
                            Json.encodeToDynamic(
                                mapOf(
                                    "title" to "Error",
                                    "text" to "Resource not found [404]",
                                    "icon" to "error"
                                )
                            )
                        )
                    } else if (throwable.code.toInt() == 422) {
                        sweetAlert.fire(
                            Json.encodeToDynamic(
                                mapOf(
                                    "title" to "Error",
                                    "text" to "Unprocessable request [422]",
                                    "icon" to "error"
                                )
                            )
                        )
                    } else if (throwable.code.toInt() >= 500) {
                        sweetAlert.fire(
                            Json.encodeToDynamic(
                                mapOf(
                                    "title" to "Error",
                                    "text" to "Server responded with error code [${throwable.code.toInt()}]",
                                    "icon" to "error"
                                )
                            )
                        )
                    } else if (throwable.code.toInt() >= 400) {
                        sweetAlert.fire(
                            Json.encodeToDynamic(
                                mapOf(
                                    "title" to "Error",
                                    "text" to "Request failed due to client error [${throwable.code.toInt()}]",
                                    "icon" to "error"
                                )
                            )
                        )
                    } else {
                        reject(throwable)
                    }
                } else {
                    reject(throwable)
                }
            }
        }
    }

    fun <T> refreshTokenAndRetry(
        req: (() -> Promise<RestResponse<T>>?)?,
        resolve: ((RestResponse<T>) -> Unit)?,
        reject: ((Throwable) -> Unit)?,
        callback: ((Boolean) -> Unit)? = null
    ) {
        when (refreshingToken) {
            true -> {
                refreshTokenObservable.subscribe {
                    if (it != null) {
                        if (it) {
                            if (req != null) {
                                req()?.then {
                                    resolve?.invoke(it)
                                }
                            }
                        }
                    }
                }
            }
            false -> {
                refreshingToken = true
                refreshTokenObservable = ObservableValue(null)

                val refreshTokenRequest = SpaApplication
                    .applicationConfiguration
                    .refreshTokenRequestProvider
                    .getRequest()

                RestClient().post<JsonObject, JsonObject>(
                    url = SpaAppEngine.restService.REFRESH_TOKEN_ENDPOINT,
                    data = refreshTokenRequest
                ).then {
                    when (SpaApplication.applicationConfiguration.authHolderType) {
                        AuthHolderType.LOCAL_STORAGE -> defaultAuthHolder.setAuth(
                            SpaApplication
                                .applicationConfiguration
                                .refreshTokenRequestProvider
                                .getAuthDataFromRefreshTokenResponse(it)
                        )
                        AuthHolderType.COOKIE -> {}
                    }

                    refreshTokenObservable.setState(true)

                    if (req != null) {
                        req()?.then {
                            resolve?.invoke(it)
                        }?.catch { throwable ->
                            if (throwable is RemoteRequestException) {
                                if (throwable.code.toInt() == 401) {
                                    SpaAppEngine.spaAuthService.logout()
                                } else {
                                    reject?.invoke(throwable)
                                }
                            } else {
                                reject?.invoke(throwable)
                            }
                        }
                    } else {
                        callback?.invoke(true);
                    }
                    refreshingToken = false
                }.catch {
                    refreshingToken = false
                    SpaAppEngine.spaAuthService.logout(oauth2LoginPage = true)
                }
            }
        }
    }
}