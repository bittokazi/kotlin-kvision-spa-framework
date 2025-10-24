package com.bittokazi.kvision.spa.framework.base.common.module

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.AuthInformationProvider
import com.bittokazi.kvision.spa.framework.base.common.ObservableManager
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.services.SpaTenantService
import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.navigo.Match
import kotlinx.browser.window
import org.w3c.dom.get

private const val COMPANY_INFO_OBSERVER_LOGIN_PAGE_TITLE = "companyInfoObserverLoginPageTitle"

class DefaultRootApplicationModule(
    val loginPage: (match: Match) -> Container,
    val securedModule: ApplicationModule,
    val authInformationProvider: AuthInformationProvider,
    vararg val routes: RouterConfiguration
): ApplicationModule {

    override fun init(): Container {
        console.log("[⌛][Startup] -> [DefaultRootApplicationModule] -> inti started")

        val authHolder = SpaAppEngine.defaultAuthHolder

        securedModule.init()

        routes.forEach { route ->
            SpaAppEngine.routing
                .on(route.route, {
                    SpaApplication.parentContainer.removeAll()
                    SpaApplication.parentContainer.add(route.view(it))
                    window.setTimeout({
                        SpaAppEngine.globalSpinnerObservable.setState(false)
                    }, 1000)
                    SpaAppEngine.authObserver.setState(false)
                    SpaAppEngine.pageTitleObserver.setState(route.title)
                })
        }

        SpaAppEngine.routing
            .on(SpaAppEngine.APP_LOGIN_ROUTE, {
                SpaApplication.parentContainer.removeAll()
                when(authHolder.getAuth()) {
                    null ->  {
                        SpaApplication.parentContainer.add(loginPage(it))
                        window.setTimeout({
                            SpaAppEngine.globalSpinnerObservable.setState(false)
                        }, 1000)
                        SpaAppEngine.authObserver.setState(false)
                        SpaAppEngine.pageTitleObserver.setState("Login")
                    }
                    else -> {
                        SpaAppEngine.routing.navigate(SpaAppEngine.APP_DASHBOARD_ROUTE)
                    }
                }
                ObservableManager.setSubscriber(COMPANY_INFO_OBSERVER_LOGIN_PAGE_TITLE) {
                    SpaTenantService.tenantInfoObserver.subscribe {
                        if(it != null) {
                            SpaAppEngine.pageTitleObserver.setState("Login | ${SpaTenantService.tenantInfo.name}")
                        }
                    }
                }
            })
            .on("${SpaAppEngine.APP_DASHBOARD_ROUTE}/*", {
                ObservableManager.setSubscriber("appModuleTenantInfoListener") {
                    SpaTenantService.tenantInfoObserver.subscribe { tenantInfo ->
                        if (tenantInfo != null && tenantInfo.enabledConfigPanel) {
                            SpaApplication.parentContainer.removeAll()
                            when(authHolder.getAuth()) {
                                null -> SpaAppEngine.routing.navigate(SpaAppEngine.APP_LOGIN_ROUTE)
                                else -> {
                                    if (SpaAppEngine.spaAuthService.spaUser == null || !SpaAppEngine.authObserver.value) {
                                        SpaAppEngine.globalSpinnerObservable.setState(true)
                                        authInformationProvider.getAuthProvider().then {
                                            SpaAppEngine.spaAuthService.spaUser = it
                                            SpaAppEngine.spaAuthService.authObservableValue.setState(it)
                                            window.setTimeout({
                                                SpaAppEngine.globalSpinnerObservable.setState(false)
                                                window["feather"].replace()
                                                window["sidebarInit"]()
                                            }, 1000)
                                            SpaAppEngine.authObserver.setState(true)
                                        }.catch { throwable ->
                                            console.log(throwable)
                                            SpaAppEngine.spaAuthService.logout()
                                        }
                                    } else {
                                        SpaAppEngine.authObserver.setState(true)
                                    }
                                }
                            }
                        } else {
                            window.setTimeout({
                                SpaAppEngine.globalSpinnerObservable.setState(false)
                            }, 1000)
                            SpaAppEngine.authObserver.setState(false)
                        }
                    }
                }
            }).addAfterHook("${SpaAppEngine.APP_DASHBOARD_ROUTE}/*") {
                if (SpaAppEngine.spaAuthService.spaUser != null) {
                    SpaAppEngine.spaAuthService.open()
                }
                SpaAppEngine.dashboardPageChangeObserver.setState("urlChanged")
                window.setTimeout({
                    window["feather"].replace()
                }, 100)
            }

        SpaAppEngine.routing.notFound({
           when(SpaApplication.applicationConfiguration.routeNotFoundActionProvider) {
               null -> SpaAppEngine.routing.navigate(SpaAppEngine.APP_LOGIN_ROUTE)
               else -> {
                   SpaApplication.parentContainer.removeAll()
                   SpaApplication.parentContainer.add(SpaApplication.applicationConfiguration.routeNotFoundActionProvider!!.doNext())
                   window.setTimeout({
                       SpaAppEngine.globalSpinnerObservable.setState(false)
                   }, 1000)
                   SpaAppEngine.authObserver.setState(false)
                   SpaAppEngine.pageTitleObserver.setState("404")

               }
           }
        })

        return Div()
    }
}