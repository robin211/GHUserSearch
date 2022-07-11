package com.astro.test.robinDharmaPutera

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainRepository: MainRepository
    val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initSetUp(){
        Dispatchers.setMain(testDispatcher)
        mainRepository = Mockito.mock(MainRepository::class.java)
        mainViewModel = MainViewModel(mainRepository)
    }

    @Test
    fun checkLoadingState_OnRequestInit_isTrue(){
        mainViewModel.setLoadingState(true)
        Truth.assertThat(mainViewModel.loading.value).isEqualTo(true)
    }

    @Test
    fun checkLoadingState_OnRequestComplete_isFalse(){
        mainViewModel.setLoadingState(false)
        Truth.assertThat(mainViewModel.loading.value).isFalse()
    }

    @Test
    fun onResponseReceived_checkFailedState_isError() = runBlockingTest{
        Mockito.`when`(mainRepository.getUsers("robin",10, 1)).thenReturn(null)
        mainViewModel.getUsers("robin",10, 1)
        Truth.assertThat(mainViewModel.errorMessage).isNotNull()
        Truth.assertThat(mainViewModel.loading.value).isEqualTo(false)
    }

    @Test
    fun onResponseReceived_checkSuccessState_isSuccess() = runBlockingTest{
        val testUser = User("robin", "avatarurl", "htmlurl")
        val listUser :List<User> = listOf(testUser)
        val testResult = Result(10, false, listUser)
        val result : Response<Result> = Response.success(200, testResult)
        Mockito.`when`(mainRepository.getUsers("robin",10, 1)).thenReturn(result)
        mainViewModel.getUsers("robin",10, 1)
        Truth.assertThat(mainViewModel.userList).isNotEqualTo(null)
        Truth.assertThat(mainViewModel.loading.value).isEqualTo(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

}