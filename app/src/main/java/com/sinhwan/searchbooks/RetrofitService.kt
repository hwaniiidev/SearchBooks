package com.sinhwan.searchbooks

import com.sinhwan.searchbooks.model.ResponseGetBooks
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {
    @GET("search/{query}/{page}")
    suspend fun getSearchedBooks(
        @Path("query") query: String,
        @Path("page") page: Int
    ): ResponseGetBooks
}