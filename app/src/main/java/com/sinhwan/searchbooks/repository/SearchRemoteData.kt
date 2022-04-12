package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

interface SearchRemoteData {

    /**
     * 책 검색
     */
    fun searchBooks(
        keyword: String,
        onSuccess: (response: ResponseGetBooks) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onFailure: (t: Throwable) -> Unit
    )
}