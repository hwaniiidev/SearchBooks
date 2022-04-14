package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

interface SearchRepository {

    /**
     * 책 검색.
     */
    fun searchBooks(
        keyword: String,
        page: Int,
        onSuccess: (response: ResponseGetBooks) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onFailure: (t: Throwable) -> Unit
    )
}