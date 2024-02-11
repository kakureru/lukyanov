package com.lukyanov.app.common.util

sealed class DataState<T> {

    class Loading<T> : DataState<T>()
    class Success<T>(val data: T) : DataState<T>()
    class Error<T>(val message: String?, val data: T? = null) : DataState<T>()

    inline fun <E : Any?> on(
        success: (T) -> E,
        loading: () -> E,
        error: (String?, T?) -> E,
    ): E {
        return when (this) {
            is Success -> success(data)
            is Loading -> loading()
            is Error -> error(message, data)
        }
    }

    inline fun <E : Any?> onSuccess(
        block: (T) -> E
    ): E? = if (this is Success) block(data) else null

    inline fun <E : Any?> onLoading(
        block: () -> E
    ): E? = if (this is Loading) block() else null

    inline fun <E : Any?> onError(
        block: (String?, T?) -> E
    ): E? = if (this is Error) block(message, data) else null
}

fun <T> T.success() = DataState.Success(this)