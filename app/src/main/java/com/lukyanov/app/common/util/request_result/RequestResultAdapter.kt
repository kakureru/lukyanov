package com.lukyanov.app.common.util.request_result

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class RequestResultAdapter<S : Any, E : Any>(
    private val successType: Type,
    private val errorBodyConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Call<RequestResult<S, E>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<RequestResult<S, E>> {
        return RequestResultCall(call, errorBodyConverter)
    }
}