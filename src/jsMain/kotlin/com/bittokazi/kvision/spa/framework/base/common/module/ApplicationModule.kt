package com.bittokazi.kvision.spa.framework.base.common.module

import io.kvision.core.Container

interface ApplicationModule {

    fun init(): Container
}