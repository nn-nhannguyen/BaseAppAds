package com.app.base.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.app.base.mvvm.arch.extensions.FlowResult
import com.app.base.mvvm.data.error.ErrorModel
import com.app.base.mvvm.model.User
import com.app.base.mvvm.repository.MainRepository
import com.app.base.mvvm.ui.api.viewmodel.TestApiViewModel
import com.app.base.mvvm.ui.usecase.GetUserUseCase
import com.app.base.mvvm.utils.NetworkHelper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class TestApiViewModelTest {

  @get:Rule
  val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()

  @MockK
  lateinit var viewModel: TestApiViewModel

  @MockK
  lateinit var mainRepository: MainRepository

  @MockK
  lateinit var networkHelper: NetworkHelper

  @MockK
  lateinit var getUserUseCase: GetUserUseCase
  private val errorObserver: Observer<ErrorModel?> = mockk(relaxed = true)

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
//    getUserUseCase = mockk()
    viewModel = TestApiViewModel(mainRepository, networkHelper, getUserUseCase)
    viewModel.errorModel.observeForever(errorObserver)
  }

  @Test
  fun getUser_isSuccess() = runTest {
    val listResult = emptyList<User>()
    every { getUserUseCase.execute(Unit) } returns flow {
      emit(FlowResult.Success(listResult))
    }
    viewModel.getUserWithUseCase()
    advanceUntilIdle()
    getUserUseCase.execute(Unit).collect {
      assertEquals(listResult, viewModel.getUserObserver.succeed.value)
    }
  }

  @Test
  fun getUser_fail() = runTest {
    every { getUserUseCase.execute(Unit) } returns flow {
      emit(FlowResult.Error(ErrorModel.LocalError("Error", "100")))
    }
    viewModel.getUserWithUseCase()
    advanceUntilIdle()
    getUserUseCase.execute(Unit).collect {
      assertEquals(ErrorModel.LocalError("Error", "100"), viewModel.getUserObserver.errorModel.value)
    }
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }
}
