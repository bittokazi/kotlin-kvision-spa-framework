package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val access_token: String,
    val refresh_token: String
)
