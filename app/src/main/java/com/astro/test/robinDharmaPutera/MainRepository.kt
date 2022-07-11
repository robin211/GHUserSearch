package com.astro.test.robinDharmaPutera

class MainRepository constructor(
    private val retrofitService: RetrofitService
    ) {
    suspend fun getUsers(username: String, perPage: Int, page: Int) = retrofitService.getUsers(username, perPage, page)
}