package com.sinhwan.searchbooks.repository

import android.util.Log
import com.sinhwan.searchbooks.ApiCall
import com.sinhwan.searchbooks.model.ResponseGetBooks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchRemoteDataImpl : SearchRemoteData{
    private val api = ApiCall.api

    override suspend fun searchBooks(
        keyword: String,
        page: Int
    ) : ResponseGetBooks {
        return api.getSearchedBooks(keyword, page)
    }

}