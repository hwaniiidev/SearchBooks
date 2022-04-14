package com.sinhwan.searchbooks.ui.search

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinhwan.searchbooks.R
import com.sinhwan.searchbooks.model.Book

@BindingAdapter("setSearchedBooks")
fun setSearchedBooks(view: RecyclerView, books: List<Book>?) {
    val adapter =
        view.adapter as? AdapterBooks ?: AdapterBooks().apply { view.adapter = this }
    if (!books.isNullOrEmpty()) {
        adapter.addItem(books)
    } else {
        adapter.clearItem()
    }
}

@BindingAdapter("loadImage")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .error(R.drawable.ic_baseline_image_not_supported_24)
        .into(view)
}

@BindingAdapter("onScrollListener")
fun setOnScrollListener(view: RecyclerView, listener: RecyclerView.OnScrollListener) {
    view.addOnScrollListener(listener)
}