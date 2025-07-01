package com.app.base.mvvm.arch.extensions

import com.app.base.mvvm.data.error.ErrorModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

@Suppress("ktlintFormat:FlowResult.kt")
sealed class FlowResult<out T> {
  data class Success<T>(val value: T) : FlowResult<T>()

  data class Error(val error: ErrorModel) : FlowResult<Nothing>()
}

suspend inline fun <T> safeUseCase(crossinline block: suspend () -> T): FlowResult<T> = try {
  FlowResult.Success(block())
} catch (e: ErrorModel) {
  FlowResult.Error(e.toError())
}

inline fun <T> safeFlow(crossinline block: suspend () -> T): Flow<FlowResult<T>> = flow {
  val result =
    try {
      val repoResult = block()
      FlowResult.Success(repoResult)
    } catch (e: ErrorModel) {
      FlowResult.Error(e)
    } catch (e: Exception) {
      FlowResult.Error(e.toError())
    }
  emit(result)
}

fun <T> observableFlow(block: suspend FlowCollector<T>.() -> Unit): Flow<FlowResult<T>> = flow(block)
  .catch { exception ->
    FlowResult.Error(exception.toError())
  }
  .map {
    FlowResult.Success(it)
  }

fun <T> Flow<FlowResult<T>>.onSuccess(action: suspend (T) -> Unit): Flow<FlowResult<T>> = transform { result ->
  if (result is FlowResult.Success<T>) {
    action(result.value)
  }
  return@transform emit(result)
}

fun <T> Flow<FlowResult<T>>.mapSuccess(): Flow<T> = transform { result ->
  if (result is FlowResult.Success<T>) {
    emit(result.value)
  }
}

fun <T> Flow<FlowResult<T>>.onError(
  normalAction: suspend (ErrorModel) -> Unit = {},
  commonAction: suspend (ErrorModel) -> Unit = {}
): Flow<FlowResult<T>> = transform { result ->
  if (result is FlowResult.Error) {
    if (!result.error.isCommonError()) {
      normalAction(result.error)
    } else {
      commonAction(result.error)
    }
  }
  return@transform emit(result)
}
