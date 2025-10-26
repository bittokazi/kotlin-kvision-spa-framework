package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.App
import com.bittokazi.kvision.spa.framework.base.services.SpaAuthService
import com.bittokazi.kvision.spa.framework.base.services.FileService
import com.bittokazi.kvision.spa.framework.base.services.RestService
import io.kvision.BootstrapIconsModule
import io.kvision.CoreModule
import io.kvision.DatetimeModule
import io.kvision.FontAwesomeModule
import io.kvision.Hot
import io.kvision.ImaskModule
import io.kvision.MapsModule
import io.kvision.RichTextModule
import io.kvision.ToastifyModule
import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.startApplication
import kotlin.js.unsafeCast

object SpaApplication {

    lateinit var applicationConfiguration: ApplicationConfiguration

    val parentContainer: Div = Div()
    var rootContainer: Container? = null

    fun init() {
        SpaAppEngine.defaultTenantHolder = AuthInformationHolder()

        SpaAppEngine.restService = RestService()
        SpaAppEngine.spaAuthService = SpaAuthService()
        SpaAppEngine.fileService = FileService()

        console.log("[⌛][Startup] [init] -> Completed")
    }

    fun start() {

        console.log("[⌛][Startup] -> SpaApplication....")

        SpaAppEngine.defaultAuthHolder = when (applicationConfiguration.authHolderType) {
            AuthHolderType.LOCAL_STORAGE -> DefaultAuthHolder()
            AuthHolderType.COOKIE -> AuthInformationHolder()
        }

        startApplication(
            ::App,
            js("import.meta.webpackHot").unsafeCast<Hot?>(),
            moduleInitializer = applicationConfiguration.moduleInitializer.toTypedArray()
        )
    }
}