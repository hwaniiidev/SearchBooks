package com.sinhwan.searchbooks.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sinhwan.searchbooks.R
import com.sinhwan.searchbooks.databinding.ItemBookBinding
import com.sinhwan.searchbooks.model.Book

class AdapterBooks : RecyclerView.Adapter<AdapterBooks.BookHolder>() {
    val books: ArrayList<Book> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding = DataBindingUtil.inflate<ItemBookBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_book,
            parent,
            false
        )
        return BookHolder(binding)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        with(books.get(position)) {
            holder.binding.book = this
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun addItem(items: List<Book>) {
        books.run {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    inner class BookHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)
}
