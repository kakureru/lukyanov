package com.lukyanov.app.common.util.request_result

import com.lukyanov.app.common.util.DataState
import java.io.IOException

typealias GenericRequestResult<T> = RequestResult<T, String>

sealed class RequestResult<out T : Any, out U : Any> {
    data class Success<T : Any>(val body: T) : RequestResult<T, Nothing>()
    data class ApiError<U : Any>(val body: U, val code: Int) : RequestResult<Nothing, U>()
    data class NetworkError(val error: IOException) : RequestResult<Nothing, Nothing>()
    data class UnknownError(val error: Throwable?) : RequestResult<Nothing, Nothing>()
}

fun <T : Any, R> GenericRequestResult<T>.mapToDataState(map: (T) -> R): DataState<R> = when (this) {
    is RequestResult.Success -> DataState.Success(map(this.body))
    is RequestResult.ApiError -> DataState.Error(this.body)
    is RequestResult.NetworkError -> DataState.Error(error.message)
    is RequestResult.UnknownError -> DataState.Error(error?.message)
}