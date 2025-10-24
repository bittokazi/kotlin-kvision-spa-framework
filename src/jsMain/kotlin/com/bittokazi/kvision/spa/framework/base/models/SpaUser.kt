package com.bittokazi.kvision.spa.framework.base.models

import kotlinx.serialization.Serializable

@Serializable
data class SpaUser(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val username: String? = null,
    val spaRoles: List<SpaRole>? = listOf(),
    var avatarImage: String? = null,
    var currentPassword: String? = null,
    var newPassword: String? = null,
    var newConfirmPassword: String? = null,
    var adminTenantUser: Boolean = false,
    var twoFaEnabled: Boolean? = null
)
