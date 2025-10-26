package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.base.common.module.ApplicationModule
import com.bittokazi.kvision.spa.framework.base.common.page.RouteNotFoundActionProvider
import com.bittokazi.kvision.spa.framework.base.common.tenant.TenantInformationProvider
import com.bittokazi.kvision.spa.framework.base.components.menu.MenuProvider
import com.bittokazi.kvision.spa.framework.base.models.SpaTenantInfo
import com.bittokazi.kvision.spa.framework.base.services.LogoutActionProvider
import io.kvision.BootstrapIconsModule
import io.kvision.CoreModule
import io.kvision.DatetimeModule
import io.kvision.FontAwesomeModule
import io.kvision.ImaskModule
import io.kvision.MapsModule
import io.kvision.ModuleInitializer
import io.kvision.RichTextModule
import io.kvision.ToastifyModule

data class ApplicationConfiguration(
    var isTenantEnabled: Boolean = false,
    var spaTenantInfo: SpaTenantInfo,
    var tenantInformationProvider: TenantInformationProvider?,
    var rootApplicationModule: ApplicationModule,
    var authHolderType: AuthHolderType,
    var menuProvider: MenuProvider,
    var logoutActionProvider: LogoutActionProvider,
    var moduleInitializer: List<ModuleInitializer> = listOf(
        DatetimeModule,
        RichTextModule,
        ImaskModule,
        ToastifyModule,
        FontAwesomeModule,
        BootstrapIconsModule,
        MapsModule,
        CoreModule
    ),
    var routeNotFoundActionProvider: RouteNotFoundActionProvider? = null
)