package com.bittokazi.kvision.spa.framework.base.common

import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.ContentContainerType
import io.kvision.core.Container
import io.kvision.navigo.Match

data class RouterConfiguration(
    val route: String,
    val view: (match: Match) -> Container,
    val dashboardContainer: ContentContainerType = ContentContainerType.CARD,
    val title: String,
)
