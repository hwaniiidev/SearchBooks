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
    val OPERATOR_OR = '|'
    val OPERATOR_NOT = '-'


    fun searchBooks() {
        val searchValue = searchKeyword.value
        if (searchValue.isNullOrBlank()) {
            _error.value = SearchError.KEYWORD_IS_NULL
            return
        }

        with(checkKeyword(searchValue)) {
            if (isNullOrEmpty()) _error.value = SearchError.KEYWORD_OVER
        }
    }

    fun checkKeyword(searchValue: String): Array<String> {
        var keyword = searchValue

        // 키워드 앞 뒤 operator 제거
        keyword.run {
            if (endsWith(OPERATOR_OR) || endsWith(OPERATOR_NOT)) {
                keyword = substring(0, lastIndex)
            }

            keyword.run {
                if (startsWith(OPERATOR_OR) || startsWith(OPERATOR_NOT)) {
                    keyword = substring(1, length)
                }
            }
        }

        // 키워드 2개 초과 에러처리
        var index = 0
        keyword.forEach {
            if (it == OPERATOR_OR || it == OPERATOR_NOT) index++
        }
        if (index > 1) {
            return arrayOf()
        }

        if (keyword.contains(OPERATOR_OR)) {
            with(keyword.split(OPERATOR_OR)) {
                return arrayOf(OPERATOR_OR.toString(), this[0] , this[1])
            }
        }

        if (keyword.contains(OPERATOR_NOT)) {
            with(keyword.split(OPERATOR_NOT)) {
                return arrayOf(OPERATOR_NOT.toString(), this[0] , this[1])
            }
        }

        return arrayOf(keyword)
    }

    fun logd(msg: String) {
        Log.d(TAG, msg)
    }

}