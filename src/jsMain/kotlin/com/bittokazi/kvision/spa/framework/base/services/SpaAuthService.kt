package com.bittokazi.kvision.spa.framework.base.services

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.ContentContainerType
import com.bittokazi.kvision.spa.framework.base.models.SpaUser
import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine.APP_LOGIN_ROUTE
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import io.kvision.state.ObservableValue
import kotlinx.browser.window

class SpaAuthService {

    var spaUser: SpaUser? = null
    val authObservableValue: ObservableValue<SpaUser?> = ObservableValue(null)
    var block: (()-> Unit)? = null

    fun logout(
        oauth2LoginPage: Boolean = false,
        fullLogout: Boolean = false
    ) {
        SpaAppEngine.defaultTenantHolder.setTenant(null)
        SpaAppEngine.defaultAuthHolder.setAuth(null)
        SpaAppEngine.globalSpinnerObservable.setState(true)
        window.document.cookie = "access_token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;"

        SpaApplication.applicationConfiguration.logoutActionProvider.logout(oauth2LoginPage, fullLogout)
    }

    fun authenticated(_block: ()-> Unit) {
        SpaAppEngine.dashboardContentContainerTypeObserver.setState(ContentContainerType.CARD)
        block = _block
    }

    fun open() {
        block?.invoke() ?: run {
            SpaAppEngine.routing.navigate(APP_LOGIN_ROUTE)
        }
        block = {}
    }

    fun switchTenant(tenant: String?, custom: () -> Unit = {}) {
        SpaAppEngine.defaultTenantHolder.setTenant(tenant)
        SpaAppEngine.defaultAuthHolder.setAuth(null)
        SpaAppEngine.globalSpinnerObservable.setState(true)
        SpaAppEngine.spaAuthService.spaUser = null
        SpaAppEngine.authObserver.setState(false)
        custom()
        SpaAppEngine.routing.navigate(APP_LOGIN_ROUTE)
    }
}

interface LogoutActionProvider {
    fun logout(
        oauth2LoginPage: Boolean = false,
        fullLogout: Boolean = false
    )
}
