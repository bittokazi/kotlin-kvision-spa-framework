package com.bittokazi.kvision.spa.framework

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.components.spinner.spinnerComponent
import com.bittokazi.kvision.spa.framework.base.layouts.errorPage
import com.bittokazi.kvision.spa.framework.base.services.SpaTenantService
import io.kvision.Application
import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.core.Position
import io.kvision.core.UNIT
import io.kvision.core.getElementJQuery
import io.kvision.core.style
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.i18n.DefaultI18nManager
import io.kvision.i18n.I18n
import io.kvision.panel.root
import io.kvision.rest.RemoteRequestException
import io.kvision.routing.Routing
import io.kvision.routing.Strategy
import kotlinx.browser.document

@JsModule("/kotlin/modules/i18n/messages-en.json")
external val messagesEn: dynamic

@JsModule("/kotlin/modules/i18n/messages-pl.json")
external val messagesPl: dynamic

class App : Application() {

    override fun start() {
        console.log("[⌛][Startup] -> Application....")

        I18n.manager =
            DefaultI18nManager(
                mapOf(
                    "en" to messagesEn,
                    "pl" to messagesPl
                )
            )

        val routing = Routing.init("/", useHash = false, strategy = Strategy.ALL)

        console.log("[⌛][Startup] -> Root route declared")

        SpaAppEngine.routing = routing

        console.log("[⌛][Startup] -> App engine initiated")

        SpaAppEngine.routing.resolve()
        root("kvapp") {

            console.log("[⌛][Startup] -> [root] -> view root started")

            SpaApplication.rootContainer = this

            val spinner = Div()
            spinner.id = "global-spinner"
            spinner.add(spinnerComponent())
            spinner.style {
                zIndex = 100000;
                position = Position.FIXED
                height = 100 to UNIT.perc
                width = 100 to UNIT.perc
                background = Background(Color("#f5f7fb"))
            }
            add(spinner)
            SpaAppEngine.globalSpinnerObservable.subscribe {
                if(it)
                    spinner.getElementJQuery()?.fadeIn(10, "linear")
                else
                    spinner.getElementJQuery()?.fadeOut(500, "linear")
            }

            add(SpaApplication.parentContainer)
            SpaApplication.parentContainer.addAfterInsertHook {
                SpaAppEngine.routing.updatePageLinks()
            }

            SpaApplication.applicationConfiguration.rootApplicationModule.init()
            SpaApplication.parentContainer.hide()

            when (SpaApplication.applicationConfiguration.isTenantEnabled) {
                true -> {
                    SpaApplication.applicationConfiguration.tenantInformationProvider!!.getTenantInfoProvider().then {
                        when (it.enabledConfigPanel) {
                            true -> {
                                SpaTenantService.tenantInfo = it
                                SpaTenantService.tenantInfoObserver.setState(it)
                                SpaApplication.parentContainer.show()
                            }
                            false -> {
                                div {
                                    add(
                                        errorPage(
                                            titleText = "403",
                                            bodyText = "Access Denied"
                                        )
                                    )
                                }
                                SpaAppEngine.pageTitleObserver.setState("403")
                            }
                        }
                    }.catch { throwable ->
                        if(throwable is RemoteRequestException) {
                            if(throwable.code.toInt() == 404) {
                                div {
                                    add(
                                        errorPage(
                                            titleText = "404",
                                            bodyText = "Resource Not Found"
                                        )
                                    )
                                }
                                SpaAppEngine.pageTitleObserver.setState("404")
                            } else {
                                div {
                                    add(
                                        errorPage(
                                            titleText = "Error",
                                            bodyText = "Service Unavailable"
                                        )
                                    )
                                }
                                SpaAppEngine.pageTitleObserver.setState("Service Unavailable")
                            }
                            SpaAppEngine.globalSpinnerObservable.setState(false)
                        }
                    }
                }
                false -> {
                    when (SpaApplication.applicationConfiguration.spaTenantInfo.enabledConfigPanel) {
                        true -> {
                            SpaTenantService.tenantInfo = SpaApplication.applicationConfiguration.spaTenantInfo
                            SpaTenantService.tenantInfoObserver.setState(SpaApplication.applicationConfiguration.spaTenantInfo)
                            SpaApplication.parentContainer.show()
                        }
                        false -> {
                            div {
                                add(
                                    errorPage(
                                        titleText = "403",
                                        bodyText = "Access Denied"
                                    )
                                )
                            }
                            SpaAppEngine.pageTitleObserver.setState("403")
                        }
                    }
                }
            }
            return@root
        }

        SpaAppEngine.pageTitleObserver.setState("Loading...")

        SpaAppEngine.pageTitleObserver.subscribe {
            document.title = it
        }

        console.log("[⌛][Startup] -> Startup sequence completed")
    }
}
