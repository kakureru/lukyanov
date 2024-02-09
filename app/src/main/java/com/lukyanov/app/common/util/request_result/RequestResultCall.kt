package com.olimppromo.utils.request_result

import com.lukyanov.app.common.util.request_result.RequestResult
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class RequestResultCall<S : Any, E : Any>(
    private val delegate: Call<S>,
    private val errorConverter: Converter<ResponseBody, E>,
) : Call<RequestResult<S, E>> {

    override fun enqueue(callback: Callback<RequestResult<S, E>>) = delegate.enqueue(
        object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()

                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(
                            this@RequestResultCall,
                            Response.success(RequestResult.Success(body))
                        )
                    } else {
                        // Response is successful but the body is null
                        callback.onResponse(
                            this@RequestResultCall,
                            Response.success(RequestResult.UnknownError(null))
                        )
                    }
                } else {
                    val errorBody = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> try {
                            errorConverter.convert(error)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    if (errorBody != null) {
                        callback.onResponse(
                            this@RequestResultCall,
                            Response.success(RequestResult.ApiError(errorBody, code))
                        )
                    } else {
                        callback.onResponse(
                            this@RequestResultCall,
                            Response.success(RequestResult.UnknownError(null))
                        )
                    }
                }
            }

            override fun onFailure(call: Call<S>, t: Throwable) {
                val requestResult = when (t) {
                    is IOException -> RequestResult.NetworkError(t)
                    else -> RequestResult.UnknownError(t)
                }
                callback.onResponse(this@RequestResultCall, Response.success(requestResult))
            }
        }
    )

    override fun clone(): Call<RequestResult<S, E>> = RequestResultCall(delegate.clone(), errorConverter)
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()

    override fun execute(): Response<RequestResult<S, E>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }
}