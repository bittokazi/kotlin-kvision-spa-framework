package com.bittokazi.kvision.spa.framework.base.services

import com.bittokazi.kvision.spa.framework.base.utils.qrCodeJs
import kotlinx.serialization.ExperimentalSerializationApi

object QrCodeService {

    @OptIn(ExperimentalSerializationApi::class)
    fun create(text: String): String {
        val qr = qrCodeJs.qrcode(7, "M");
        qr.addData(text)
        qr.make()
        return qr.createImgTag(7)
    }
}
