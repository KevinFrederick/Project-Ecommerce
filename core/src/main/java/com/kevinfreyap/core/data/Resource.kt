package com.kevinfreyap.core.data

sealed class Resource<T>(
    open val data: T? = null,
    val message: String? = null
) {
    class Success<T>(override val data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data = data, message = message)
    class Loading<T>(data: T? = null): Resource<T>(data)
}