package com.bittokazi.kvision.spa.framework.base.common.module

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.layouts.errorPage
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import io.kvision.core.Container

class DefaultSecuredPageModule(
    vararg val routes: RouterConfiguration
): PageModule {
    override fun init(layoutContainer: Container) {
        routes.forEach { routerConfiguration ->
            SpaAppEngine.routing
                .on(routerConfiguration.route, { match ->
                    SpaAppEngine.spaAuthService.authenticated { spaUser ->
                        console.log("[\uD83D\uDCC4\u200B][DefaultSecuredModule] -> Open page ->", routerConfiguration.route)
                        layoutContainer.removeAll()
                        val hasAccess = SpaApplication.applicationConfiguration.menuProvider
                            .hasAccessToPage(spaUser.spaRoles.orEmpty(), routerConfiguration.route)
                        if (hasAccess) {
                            layoutContainer.add(routerConfiguration.view(match))
                            SpaAppEngine.dashboardContentContainerTypeObserver.setState(routerConfiguration.dashboardContainer)
                            SpaAppEngine.pageTitleObserver.setState(routerConfiguration.title)
                        } else {
                            layoutContainer.add(errorPage(titleText = "403", bodyText = "Access Denied"))
                            SpaAppEngine.pageTitleObserver.setState("403")
                        }
                    }
                })
        }
    }
}