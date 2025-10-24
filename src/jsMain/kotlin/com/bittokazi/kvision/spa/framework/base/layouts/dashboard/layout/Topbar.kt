package com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import io.kvision.core.Container
import io.kvision.html.*
import io.kvision.state.bind
import kotlinx.browser.window
import kotlinx.dom.removeClass

fun Container.dashboardTopBar() {
    nav(className = "navbar navbar-expand navbar-light navbar-bg") {
        link("", className = "sidebar-toggle js-sidebar-toggle") {
            add(i(className = "hamburger align-self-center"))
        }
        div(className = "navbar-collapse collapse") {
            ul(className = "navbar-nav navbar-align") {
                li(className = "nav-item dropdown") {
                    link("", "#", className = "nav-icon dropdown-toggle d-inline-block d-sm-none") {
                        setAttribute("data-bs-toggle", "dropdown")
                        add(i(className = "align-middle") {
                            setAttribute("data-feather", "settings")
                        })
                    }
                    link("", "#", className = "nav-link dropdown-toggle d-none d-sm-inline-block") {
                        setAttribute("data-bs-toggle", "dropdown")
                        add(
                            image(
                                SpaAppEngine.spaAuthService.spaUser?.avatarImage,
                                SpaAppEngine.spaAuthService.spaUser?.email,
                                className = "avatar img-fluid rounded me-1"
                            )
                        )
                        add(span(className = "text-dark").bind(SpaAppEngine.spaUserInfoChangeObserver) {
                            content = "${SpaAppEngine.spaAuthService.spaUser?.email}"
                        })
                    }
                    div(className = "dropdown-menu dropdown-menu-end").bind(SpaAppEngine.dashboardPageChangeObserver) {
                        setAttribute("id", "topBarDropdownMenu")
                        window.document.getElementById("topBarDropdownMenu")?.removeClass("show")

                        link("", SpaAppEngine.APP_DASHBOARD_ACCOUNT_SETTINGS_ROUTE, className = "dropdown-item", dataNavigo = true) {
                            add(i(className = "align-middle me-1") {
                                setAttribute("data-feather", "user")
                            })
                            add(span {
                                content = "Account"
                            })
                            if (!SpaAppEngine.defaultTenantHolder.getTenant().isNullOrEmpty()) hide()
                        }
                        div(className = "dropdown-divider") {
                            if (!SpaAppEngine.defaultTenantHolder.getTenant().isNullOrEmpty()) hide()
                        }
                        link("", SpaAppEngine.APP_DASHBOARD_ACCOUNT_SECURITY_ROUTE, className = "dropdown-item", dataNavigo = true) {
                            add(i(className = "align-middle me-1") {
                                setAttribute("data-feather", "settings")
                            })
                            add(span {
                                content = "Settings"
                            })
                            if (!SpaAppEngine.defaultTenantHolder.getTenant().isNullOrEmpty()) hide()
                        }
                        link("", className = "dropdown-item") {
                            add(i(className = "align-middle me-1") {
                                setAttribute("data-feather", "log-in")
                            })
                            add(span {
                                content = "Switch Back"
                            })
                            onClick {
                                SpaAppEngine.spaAuthService.switchTenant(null)
                            }
                            if (SpaAppEngine.defaultTenantHolder.getTenant().isNullOrEmpty()) hide()
                        }
                        div(className = "dropdown-divider")
                        link("", className = "dropdown-item") {
                            add(span {
                                content = "Logout"
                            })
                        }.onClick {
                            SpaAppEngine.spaAuthService.logout(fullLogout = true)
                        }
                    }
                }
            }
        }
    }
}
