package com.example.marvelapp.presentation.extension

import com.example.core.usecase.base.ResultStatus
import kotlinx.coroutines.flow.Flow

suspend fun <T> Flow<ResultStatus<T>>.watchStatus(
    loading: suspend () -> Unit = {},
    error: suspend (throwable: Throwable) -> Unit,
    success: suspend (data: T) -> Unit
) {
    collect { status ->
        when (status) {
            ResultStatus.Loading -> loading.invoke()
            is ResultStatus.Error -> error.invoke(status.throwable)
            is ResultStatus.Success -> success.invoke(status.data)
        }
    }
}