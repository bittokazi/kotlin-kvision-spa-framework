package com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.ObservableManager
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h5
import io.kvision.html.main

private const val DASHBOARD_CONTENT = "dashboardContent"

fun Container.dashboardContent(
    container: Container
) {
    main(className = "content") {
        div(className = "container-fluid p-0") {
            ObservableManager.setSubscriber(DASHBOARD_CONTENT) {
                SpaAppEngine.dashboardContentContainerTypeObserver.subscribe { contentContainerType ->
                    removeAll()
                    h1(className = "h3 mb-3", content = "Dashboard")
                    when (contentContainerType) {
                        ContentContainerType.CARD -> {
                            add(
                                div(className = "row") {
                                    div(className = "col-12") {
                                        div(className = "card") {
                                            div(className = "card-header") {
                                                h5(className = "card-title mb-0") {
                                                    SpaAppEngine.pageTitleObserver.subscribe {
                                                        content = it
                                                    }
                                                }
                                                div(className = "card-body") {
                                                    add(container)
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        ContentContainerType.NO_CARD -> add(container)
                    }
                }
            }
        }
    }
}

enum class ContentContainerType {
    CARD, NO_CARD
}
