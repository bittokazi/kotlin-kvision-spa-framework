package com.bittokazi.kvision.spa.framework.base.utils

import io.kvision.utils.useModule

@JsModule("kotlin-kvision-spa-framework-resources/static/js/app.js")
external val appJs: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/js/qrcode.min.js")
external val qrCodeJs: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/css/app.css")
external val appCss: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/css/custom.css")
external val customCss: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/img/logo.png")
external val logoPng: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/css/google-fonts.css")
external val googleFontsCss: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/js/sweetalert2.js")
external val sweetAlert: dynamic

@JsModule("kotlin-kvision-spa-framework-resources/static/js/moment.min.js")
external val momentJs: dynamic

fun importDefaultResources() {
    useModule(appJs)
    useModule(qrCodeJs)
    useModule(sweetAlert)
    useModule(momentJs)
    useModule(appCss)
    useModule(customCss)
    useModule(googleFontsCss)
    useModule(logoPng)
}
