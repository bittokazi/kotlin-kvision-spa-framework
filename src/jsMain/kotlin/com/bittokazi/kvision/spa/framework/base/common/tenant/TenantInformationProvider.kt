package com.bittokazi.kvision.spa.framework.base.common.tenant

import com.bittokazi.kvision.spa.framework.base.models.SpaTenantInfo
import kotlin.js.Promise

interface TenantInformationProvider {
    fun getTenantInfoProvider(): Promise<SpaTenantInfo>
}