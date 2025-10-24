package com.bittokazi.kvision.spa.framework.base.common.module

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import io.kvision.core.Container

class DefaultSecuredPageModule(
    vararg val routes: RouterConfiguration
): PageModule {
    override fun init(layoutContainer: Container) {
        routes.forEach { routerConfiguration ->
            SpaAppEngine.routing
                .on(routerConfiguration.route, {
                    SpaAppEngine.spaAuthService.authenticated {
                        layoutContainer.removeAll()
                        layoutContainer.add(routerConfiguration.view(it))
                        SpaAppEngine.dashboardContentContainerTypeObserver.setState(routerConfiguration.dashboardContainer)
                        SpaAppEngine.pageTitleObserver.setState(routerConfiguration.title)
                    }
                })
        }
    }
}