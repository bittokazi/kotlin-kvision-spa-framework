package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.json.JsonObject

interface RefreshTokenRequestProvider {
    fun getRequest(): JsonObject
}
