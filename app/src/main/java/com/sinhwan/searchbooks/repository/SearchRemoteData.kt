package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

interface SearchRemoteData {

    /**
     * 책 검색
     */
    suspend fun searchBooks(
        keyword: String,
        page: Int
    ): ResponseGetBooks
}