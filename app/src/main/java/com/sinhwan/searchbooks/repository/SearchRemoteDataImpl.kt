package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.ApiCall
import com.sinhwan.searchbooks.model.ResponseGetBooks


class SearchRemoteDataImpl : SearchRemoteData{
    private val api = ApiCall.api

    override suspend fun searchBooks(
        keyword: String,
        page: Int
    ) : ResponseGetBooks {
        return api.getSearchedBooks(keyword, page)
    }

}