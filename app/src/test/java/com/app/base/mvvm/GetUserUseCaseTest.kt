package com.app.base.mvvm

import androidx.lifecycle.Observer
import com.app.base.mvvm.model.User
import com.app.base.mvvm.network.api.ApiHelper
import com.app.base.mvvm.repository.MainRepository
import com.app.base.mvvm.ui.api.viewmodel.TestApiViewModel
import com.app.base.mvvm.ui.usecase.GetUserUseCase
import com.app.base.mvvm.utils.NetworkHelper
import io.mockk.impl.annotations.MockK
import io.mockk.mockkClass
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetUserUseCaseTest {
  private lateinit var getUserUseCase: GetUserUseCase
  private lateinit var apiHelper: ApiHelper
  private lateinit var networkHelper: NetworkHelper
  private lateinit var mainRepository: MainRepository

  @MockK
  private lateinit var mockObserver: Observer<List<User>>

  @Before
  fun setUp() {
    apiHelper = mockkClass(ApiHelper::class)
    networkHelper = mockkClass(NetworkHelper::class)
    mainRepository = mockkClass(MainRepository::class)
    getUserUseCase = GetUserUseCase(
      mainRepository = mainRepository
    )
  }

//  @Test
//  fun getUserInfoTest() {
//    val viewModel = TestApiViewModel(mainRepository, networkHelper, getUserUseCase)
//    viewModel.fetchUsers()
//    val mockResponse = User(
//      id = 1,
//      name = "Nguyen Van Anh",
//      email = "anhnv@gmail.com",
//      avatar = ""
//    )
//
//    every { mainRepository.getUsersSafe() } returns Single.just(mockResponse)
//
// //    val testObserver = TestObserver<User>()
// //    useCase.execute(createSingleObserver(testObserver), Unit)
// //    testObserver.assertValue { it == mockResponse }
// //    assertEquals()
//
//    runBlocking {
//      viewModel.getUserWithUseCase()
// //      viewMode.u.observeForever(mockObserver)
//      viewModel.users.observeForever(mockObserver)
//
//      verify(mockObserver).onChanged(captor.capture())
//      assertEquals(true, captor.value.loading)
//      coroutineScope.advanceTimeBy(10)
//      verify(mockObserver, times(2)).onChanged(captor.capture())
//      assertEquals("Pavneet", captor.value.data[0].name)// name is custom implementaiton field of `ChocolateModel` class
//    }
//  }

  @Test
  fun givenServerReturnSuccess() {
    runBlocking {
      doReturn(flowOf(emptyList<User>())).`when`(apiHelper).getUsersSafe()
      val viewModel = TestApiViewModel(mainRepository, networkHelper, getUserUseCase)
      viewModel.users.observeForever {
        assertEquals(emptyList<List<User>>(), it.data)
//        cancelAndIgnoreRemainingEvents()
      }
      verify(apiHelper).getUsers()
    }
  }
}
