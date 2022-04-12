package com.sinhwan.searchbooks.ui.search

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sinhwan.searchbooks.model.Book

@BindingAdapter("setSearchedBooks")
fun setSearchedBooks(view: RecyclerView, books: List<Book>?) {
    val adapter =
        view.adapter as? AdapterBooks ?: AdapterBooks().apply { view.adapter = this }
    if (!books.isNullOrEmpty()) {
        adapter.addItem(books)
    }
}