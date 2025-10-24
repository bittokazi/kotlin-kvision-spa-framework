package com.bittokazi.kvision.spa.framework.base.common.module

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.layouts.DefaultLayoutLoader
import com.bittokazi.kvision.spa.framework.base.layouts.LayoutLoader
import io.kvision.core.Container
import io.kvision.html.Div

class DefaultSecuredModule(
    val layoutLoader: LayoutLoader = DefaultLayoutLoader(),
    val modules: List<PageModule>,
    vararg val routes: RouterConfiguration
): ApplicationModule {

    private val self = Div()

    override fun init(): Container {
        console.log("[⌛][Startup] -> [DefaultSecuredModule] -> inti started")

        val container = Div()
        container.addAfterInsertHook {
            SpaAppEngine.routing.updatePageLinks()
        }

        val layout = Div()
        layout.addAfterInsertHook {
            SpaAppEngine.routing.updatePageLinks()
        }

        SpaApplication.rootContainer!!.add(layout)

        SpaAppEngine.authObserver.subscribe {
            if(it && layout.getChildren().isEmpty()) {
                console.log("[\uD83D\uDCC4\u200B][DefaultSecuredModule] -> Layout init stater")
                layout.removeAll()
                layout.add(layoutLoader.load(container))
                if (SpaAppEngine.spaAuthService.spaUser != null) {
                    console.log("[\uD83D\uDD13\u200B][DefaultSecuredModule] -> Opening secure page")
                    SpaAppEngine.spaAuthService.open()
                }

                console.log("[\uD83D\uDCC4\u200B][DefaultSecuredModule] -> Layout initiated")
            } else if (!it) {
                layout.removeAll()
                console.log("[\uD83D\uDDD1\uFE0F\u200B][DefaultSecuredModule] -> Removed secure page layout")
            }
        }

        modules.forEach { module ->
            module.init(container)
        }

        routes.forEach { routerConfiguration ->
            SpaAppEngine.routing
                .on(routerConfiguration.route, {
                    SpaAppEngine.spaAuthService.authenticated {
                        container.removeAll()
                        container.add(routerConfiguration.view(it))
                        SpaAppEngine.dashboardContentContainerTypeObserver.setState(routerConfiguration.dashboardContainer)
                        SpaAppEngine.pageTitleObserver.setState(routerConfiguration.title)
                    }
                })
        }

        return self
    }
}