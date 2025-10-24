package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.base.models.SpaUser
import kotlin.js.Promise

interface AuthInformationProvider {
    fun getAuthProvider(): Promise<SpaUser>
}