package com.sinhwan.searchbooks.ui

import com.sinhwan.searchbooks.model.Book

interface BookClickCallback {
    fun onClick(book: Book)
}