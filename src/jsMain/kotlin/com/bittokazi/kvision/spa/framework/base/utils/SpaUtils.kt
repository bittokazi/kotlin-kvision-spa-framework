package com.bittokazi.kvision.spa.framework.base.utils

object SpaUtils {

    val moment = momentJs

    fun formatTimeFromNow(dateTime: String?): String? {
        return dateTime?.let { moment(dateTime).local().fromNow() } ?: run { "" }
    }
}
