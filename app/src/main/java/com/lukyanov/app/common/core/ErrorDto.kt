package com.lukyanov.app.common.core

import kotlinx.serialization.Serializable

@Serializable
class ErrorDto(
    val message: String,
)