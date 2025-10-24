package com.bittokazi.kvision.spa.framework.base.common.page

import io.kvision.core.Component

interface RouteNotFoundActionProvider {
    fun doNext(): Component
}