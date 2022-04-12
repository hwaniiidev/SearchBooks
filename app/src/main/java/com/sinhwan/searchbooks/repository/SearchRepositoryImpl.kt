package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

class SearchRepositoryImpl : SearchRepository{
    private val searchRemoteData = SearchRemoteDataImpl()

    override fun searchBooks(
        keyword: String,
        onSuccess: (response: ResponseGetBooks) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onFailure: (t: Throwable) -> Unit,
    ) {
        searchRemoteData.searchBooks(keyword, onSuccess, onError, onFailure)
    }

}