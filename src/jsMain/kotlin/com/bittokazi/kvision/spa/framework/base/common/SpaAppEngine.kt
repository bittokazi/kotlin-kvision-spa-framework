package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.ContentContainerType
import com.bittokazi.kvision.spa.framework.base.models.SpaUser
import com.bittokazi.kvision.spa.framework.base.services.SpaAuthService
import com.bittokazi.kvision.spa.framework.base.services.FileService
import com.bittokazi.kvision.spa.framework.base.services.RestService
import io.kvision.navigo.Navigo
import io.kvision.state.ObservableValue

object SpaAppEngine {
    lateinit var routing: Navigo
    lateinit var defaultAuthHolder: AuthHolder
    lateinit var defaultTenantHolder: TenantHolder
    lateinit var restService: RestService
    lateinit var spaAuthService: SpaAuthService
    lateinit var fileService: FileService

    var globalSpinnerObservable: ObservableValue<Boolean> = ObservableValue(true)
    var authObserver: ObservableValue<Boolean> = ObservableValue(false)
    var pageTitleObserver: ObservableValue<String> = ObservableValue("")
    var dashboardPageChangeObserver: ObservableValue<String> = ObservableValue("")
    var dashboardContentContainerTypeObserver: ObservableValue<ContentContainerType> = ObservableValue(
        ContentContainerType.CARD
    )
    var spaUserInfoChangeObserver: ObservableValue<SpaUser?> = ObservableValue(null)

    var APP_BASE_ROUTE = "/app"
    var APP_DASHBOARD_ROUTE = "$APP_BASE_ROUTE/dashboard"

    var APP_LOGIN_ROUTE = "$APP_BASE_ROUTE/login"

    var APP_DASHBOARD_ACCOUNT_SETTINGS_ROUTE = "$APP_DASHBOARD_ROUTE/account-settings"
    var APP_DASHBOARD_ACCOUNT_SECURITY_ROUTE = "$APP_DASHBOARD_ACCOUNT_SETTINGS_ROUTE/security"
}
