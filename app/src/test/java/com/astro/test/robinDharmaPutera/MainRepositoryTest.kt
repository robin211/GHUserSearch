package com.astro.test.robinDharmaPutera

import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import retrofit2.Response

class MainRepositoryTest {
    @Mock
    lateinit var repoInstance: MainRepository

    @Before
    fun init() {
        repoInstance = Mockito.mock(MainRepository::class.java)
    }

    @Test
    fun validateGetuser_isSuccess() {
        val testUser = User("robin", "avatarurl", "htmlurl")
        val listUser :List<User> = listOf(testUser)
        val testResult = Result(10, false, listUser)
        val result : Response<Result> = Response.success(200, testResult)
        runBlocking {
            Mockito.`when`(repoInstance.getUsers("robin",10, 1)).thenReturn(
                result
            )
        }

        runBlocking {
            Truth.assertThat(repoInstance.getUsers("robin",10, 1))
                .isEqualTo(result)
        }
    }

    @Test
    fun validateGetUser_isFailed() {
        runBlocking {
            Mockito.`when`(repoInstance.getUsers("robin",10, 1)).thenReturn(
                null
            )
        }

        runBlocking {
            Truth.assertThat(repoInstance.getUsers("robin",10, 1))
                .isEqualTo(null)
        }
    }
}