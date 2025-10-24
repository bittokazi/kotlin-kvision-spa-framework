package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.Serializable

@Serializable
data class SpaRole(
    val id: String? = null,
    val title: String? = "",
    val name: String? = "",
    val description: String? = ""
)
