package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

class SearchRepositoryImpl : SearchRepository{
    private val searchRemoteData = SearchRemoteDataImpl()

    override suspend fun searchBooks(
        keyword: String,
        page: Int
    ) : ResponseGetBooks {
        return searchRemoteData.searchBooks(keyword, page)
    }

}