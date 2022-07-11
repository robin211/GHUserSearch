package com.astro.test.robinDharmaPutera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val userList = MutableLiveData<List<User>>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>(false)

    fun getUsers(username: String, perPage: Int, page: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = mainRepository.getUsers(username, perPage, page)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    loading.value = false
                    userList.postValue(response.body()?.items)
                    errorMessage.value = null
                } else {
                    userList.postValue(ArrayList())
                    errorMessage.value = "Error getting data"
                }
            }
        }

    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun setLoadingState(state: Boolean) {
        loading.postValue(state)
    }

}