package com.sinhwan.searchbooks.ui.search

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sinhwan.searchbooks.R
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.ui.BookClickCallback

@BindingAdapter("setSearchedBooks", "clickCallback")
fun setSearchedBooks(view: RecyclerView, books: List<Book>?, clickCallback: BookClickCallback) {
    val adapter =
        view.adapter as? AdapterBooks ?: AdapterBooks(clickCallback).apply {
            setHasStableIds(true)
            view.adapter = this
        }
    if (!books.isNullOrEmpty()) {
        adapter.addItem(books)
    } else {
        adapter.clearItem()
    }
}

@BindingAdapter("loadImage", "progress")
fun loadImage(view: ImageView, url: String, progress: ProgressBar) {
    progress.visibility = View.VISIBLE
    Glide.with(view)
        .load(url)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                progress.visibility = View.GONE
                return false
            }
        })
        .error(R.drawable.ic_baseline_image_not_supported_24)
        .into(view)

}

@BindingAdapter("onScrollListener")
fun setOnScrollListener(view: RecyclerView, listener: RecyclerView.OnScrollListener) {
    view.addOnScrollListener(listener)
}

@BindingAdapter("setVisibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}