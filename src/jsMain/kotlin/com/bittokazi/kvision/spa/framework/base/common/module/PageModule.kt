package com.bittokazi.kvision.spa.framework.base.common.module

import io.kvision.core.Container

interface PageModule {
    fun init(layoutContainer: Container)
}