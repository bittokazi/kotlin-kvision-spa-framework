package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.Serializable

@Serializable
data class SpaTenantInfo (
    val cpanel: Boolean = true,
    val enabledConfigPanel: Boolean = true,
    val name: String = "",
    val systemVersion: String = "v0.0.0_Dev"
)
