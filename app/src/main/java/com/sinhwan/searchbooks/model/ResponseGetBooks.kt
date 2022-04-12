package com.sinhwan.searchbooks.model

data class ResponseGetBooks(
    val books: List<Book>,
    val error: String,
    val page: String,
    val total: String
)
