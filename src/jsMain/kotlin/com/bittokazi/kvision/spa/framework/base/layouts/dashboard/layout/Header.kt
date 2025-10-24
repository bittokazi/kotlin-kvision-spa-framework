package com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.ObservableManager
import com.bittokazi.kvision.spa.framework.base.services.SpaTenantService
import io.kvision.core.Container
import io.kvision.html.*

private const val TOP_BAR_MENU_CLICK = "topBarMenuCLick"

fun Container.dashboardHeader() {
    val nav = Nav(className = "sidebar js-sidebar collapsed") {
        id="sidebar"
        div(className = "sidebar-content js-simplebar") {
            link("", SpaAppEngine.APP_DASHBOARD_ROUTE, className = "sidebar-brand", dataNavigo = true) {
                add(span(className = "align-middle", content = SpaTenantService.tenantInfo.name))
            }
            dashboardMenuBar()
        }
    }
    add(nav)

    ObservableManager.setSubscriber(TOP_BAR_MENU_CLICK) {
        SpaAppEngine.dashboardPageChangeObserver.subscribe {
            nav.addCssClass("collapsed")
            nav.removeCssClass("collapsed")
        }
    }
}
