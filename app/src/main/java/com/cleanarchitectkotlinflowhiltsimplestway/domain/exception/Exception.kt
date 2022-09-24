package com.cleanarchitectkotlinflowhiltsimplestway.domain.exception

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.resolveError(): Throwable {
    return when(this) {
        is SocketTimeoutException ->  NetworkErrorException(errorMessage = "connection error!")
        is ConnectException ->  NetworkErrorException(errorMessage = "no internet access!")
        is UnknownHostException ->  NetworkErrorException(errorMessage = "no internet access!")
        is HttpException -> when(this.code()) {
            502 -> NetworkErrorException(this.code(),  "internal error!")
            401 -> AuthenticationException("authentication error!")
            400 -> NetworkErrorException.parseException(this)
            404 -> NetworkErrorException(this.code(), "Not found URL, please check your endpoint")
            else -> this
        }
        else -> this
    }
}