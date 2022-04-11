package com.sinhwan.searchbooks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinhwan.searchbooks.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
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
        }


    }
}