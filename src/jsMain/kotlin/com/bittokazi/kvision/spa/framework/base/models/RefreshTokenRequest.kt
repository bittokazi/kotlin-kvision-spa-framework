package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val refresh_token: String
)
