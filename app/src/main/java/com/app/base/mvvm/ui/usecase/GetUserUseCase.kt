package com.app.base.mvvm.ui.usecase

import com.app.base.mvvm.arch.extensions.FlowResult
import com.app.base.mvvm.model.User
import com.app.base.mvvm.repository.MainRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserUseCase @Inject constructor(
  private val mainRepository: MainRepository
) : SingleUseCase<List<User>, Unit>() {

  override fun buildUseCase(params: Unit): Flow<FlowResult<List<User>>> {
    return mainRepository.getUsersSafe()
  }
}
