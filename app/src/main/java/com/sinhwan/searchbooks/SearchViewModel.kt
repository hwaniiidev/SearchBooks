package com.sinhwan.searchbooks

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    val TAG = this::class.java.simpleName
    val searchKeyword = MutableLiveData<String>()
    private val _error = MutableLiveData<SearchError>()
    val error: LiveData<SearchError> = _error


    fun searchBooks() {
        val searchValue = searchKeyword.value
        if (searchValue.isNullOrBlank()) {
            _error.value = SearchError.KEYWORD_IS_NULL
        }
    }

    fun logd(msg: String) {
        Log.d(TAG, msg)
    }

}