package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.base.common.module.ApplicationModule
import com.bittokazi.kvision.spa.framework.base.common.page.RouteNotFoundActionProvider
import com.bittokazi.kvision.spa.framework.base.common.tenant.TenantInformationProvider
import com.bittokazi.kvision.spa.framework.base.components.menu.MenuProvider
import com.bittokazi.kvision.spa.framework.base.models.SpaTenantInfo
import com.bittokazi.kvision.spa.framework.base.services.LogoutActionProvider

data class ApplicationConfiguration(
    var isTenantEnabled: Boolean = false,
    var spaTenantInfo: SpaTenantInfo,
    var tenantInformationProvider: TenantInformationProvider?,
    var rootApplicationModule: ApplicationModule,
    var authHolderType: AuthHolderType,
    var menuProvider: MenuProvider,
    var logoutActionProvider: LogoutActionProvider,
    var routeNotFoundActionProvider: RouteNotFoundActionProvider? = null
)