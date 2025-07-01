package com.app.base.mvvm.ui.usecase

import com.app.base.mvvm.arch.extensions.FlowResult
import com.app.base.mvvm.model.DataResponse
import com.app.base.mvvm.repository.MainRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LoginUseCase @Inject constructor(
  private val mainRepository: MainRepository
) : SingleUseCase<DataResponse<Any?>, LoginUseCase.Param>() {

  data class Param(
    val userName: String,
    val password: String
  )

  override fun buildUseCase(params: Param): Flow<FlowResult<DataResponse<Any?>>> {
    return mainRepository.login(params.userName, params.password)
  }
}
