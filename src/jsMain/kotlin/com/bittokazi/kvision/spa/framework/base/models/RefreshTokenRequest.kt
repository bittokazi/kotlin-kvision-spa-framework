package com.bittokazi.kvision.spa.framework.base.models

import com.bittokazi.kvision.spa.framework.base.common.AuthData
import kotlinx.serialization.json.JsonObject

interface RefreshTokenRequestProvider {
    fun getRequest(): JsonObject
    fun getAuthDataFromRefreshTokenResponse(): AuthData
}
