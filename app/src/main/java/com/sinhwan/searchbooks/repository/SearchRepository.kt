package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.model.ResponseGetBooks

interface SearchRepository {

    /**
     * 책 검색
     *
     * @param keyword 검색 키워드
     * @param page    검색 page
     */
    suspend fun searchBooks(
        keyword: String,
        page: Int
    ) : ResponseGetBooks
}