package com.bittokazi.kvision.spa.framework.base.services

import com.bittokazi.kvision.spa.framework.base.models.SpaTenantInfo
import io.kvision.state.ObservableValue

object SpaTenantService {

    lateinit var tenantInfo: SpaTenantInfo
    val tenantInfoObserver: ObservableValue<SpaTenantInfo?> = ObservableValue(null)
}
