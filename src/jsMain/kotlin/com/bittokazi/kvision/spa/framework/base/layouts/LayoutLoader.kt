package com.bittokazi.kvision.spa.framework.base.layouts

import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.dashboardContent
import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.dashboardFooter
import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.dashboardHeader
import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.dashboardTopBar
import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.div

interface LayoutLoader {
    fun load(contentContainer: Container): Container
}

class DefaultLayoutLoader: LayoutLoader {
    override fun load(contentContainer: Container): Container {
        return Div(className = "wrapper") {
            dashboardHeader()
            div(className = "main") {
                dashboardTopBar()
                dashboardContent(contentContainer)
                dashboardFooter()
            }
        }
    }
}
