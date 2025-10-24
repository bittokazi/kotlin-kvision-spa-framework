package com.bittokazi.kvision.spa.framework.base.models

sealed class SpaResult<T, E> {

    data class Success<T, E>(
        val outcome: T
    ) : SpaResult<T, E>()

    data class Failure<T, E>(
        val errorCode: E,
        val errorMessage: String = "",
        val cause: Throwable? = null
    ) : SpaResult<T, E>()
}
