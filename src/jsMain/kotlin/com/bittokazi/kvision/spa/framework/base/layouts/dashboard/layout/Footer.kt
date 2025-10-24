package com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.services.SpaTenantService
import io.kvision.core.Container
import io.kvision.html.*

fun Container.dashboardFooter() {
    footer(className = "footer") {
        div(className = "container-fluid") {
            div(className = "row text-muted") {
                div(className = "col-6 text-start") {
                    link("", SpaAppEngine.APP_DASHBOARD_ROUTE, dataNavigo = true) {
                        add(strong {
                            content = SpaTenantService.tenantInfo.name
                        })
                    }
                    add(span {
                        content = "&nbsp; &copy;"
                        rich = true
                    })
                }
                div(className = "col-6 text-end") {
                    ul(className = "list-inline") {
                        li(className = "list-inline-item") {
                            link(SpaTenantService.tenantInfo.systemVersion, className = "text-muted")
                        }
                    }
                }
            }
        }
    }
}
