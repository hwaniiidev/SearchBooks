package com.sinhwan.searchbooks.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinhwan.searchbooks.R
import com.sinhwan.searchbooks.databinding.ActivitySearchBinding
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.ui.BookClickCallback
import com.sinhwan.searchbooks.ui.KEY_DETAIL_BOOK
import com.sinhwan.searchbooks.ui.detailbook.DetailBookActivity
import com.sinhwan.searchbooks.ui.toast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    private lateinit var imm: InputMethodManager

    private val bookClickCallback = object : BookClickCallback {
        override fun onClick(book: Book) {
            with(Intent(this@SearchActivity, DetailBookActivity::class.java)) {
                putExtra(KEY_DETAIL_BOOK, book)
                startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelProvider = ViewModelProvider(this, object  : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel() as T
            }
        })
        val viewModel = viewModelProvider[SearchViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.apply {
            this.viewModel = viewModel
            lifecycleOwner = this@SearchActivity
            clickCallback = bookClickCallback
        }

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        viewModel.error.observe(this) {
            toast(it)
        }

        viewModel.hideKeyboard.observe(this) {
            imm.hideSoftInputFromWindow(binding.editKeyword.windowToken, 0)
        }
    }
}