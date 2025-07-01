package com.app.base.mvvm.ui.usecase

import com.app.base.mvvm.arch.extensions.FlowResult
import kotlinx.coroutines.flow.Flow

/**
 * Abstract class for a UseCase.
 */
abstract class SingleUseCase<T, in Params> {

  protected abstract fun buildUseCase(params: Params): Flow<FlowResult<T>>

  open fun execute(params: Params): Flow<FlowResult<T>> {
    return this.buildUseCase(params)
  }
}
