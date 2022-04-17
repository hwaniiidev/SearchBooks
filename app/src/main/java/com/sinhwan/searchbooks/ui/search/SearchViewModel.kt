package com.sinhwan.searchbooks.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.repository.SearchRepositoryImpl

class SearchViewModel : ViewModel() {
    val TAG = this::class.java.simpleName

    var currentPage = 1
    var isLoading = false

    lateinit var convertedKeywords: Array<String>

    val searchKeyword = MutableLiveData<String>()
    private val _error = MutableLiveData<SearchError>()
    val error: LiveData<SearchError> = _error
    private val _searchedBooks = MutableLiveData<List<Book>>()
    val searchedBooks: LiveData<List<Book>> = _searchedBooks

    val OPERATOR_OR = '|'
    val STR_OPERATOR_OR = OPERATOR_OR.toString()
    val OPERATOR_NOT = '-'
    val STR_OPERATOR_NOT = OPERATOR_NOT.toString()
    val searchRepository = SearchRepositoryImpl()

    val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(view, dx, dy)
            with((view.layoutManager!!) as LinearLayoutManager) {
                if (!isLoading && findLastCompletelyVisibleItemPosition() > (itemCount - 5)) {
                    currentPage++
                    isLoading = true
                    checkOperatorSearch(convertedKeywords)
                }
            }
        }
    }

    fun onClickSearchButton() {
        _searchedBooks.value = null
        currentPage = 1
        val searchValue = searchKeyword.value
        if (searchValue.isNullOrBlank()) {
            _error.value = SearchError.KEYWORD_IS_NULL
            return
        }

        with(checkKeyword(searchValue)) {
            if (isNullOrEmpty()) {
                _error.value = SearchError.KEYWORD_OVER
                return
            }

            if (size > 1 && this[1].lowercase().equals(this[2].lowercase())) {
                if (this[0].equals(STR_OPERATOR_OR)) {
                    _error.value = SearchError.KEYWORD_SAME_OR
                    convertedKeywords = arrayOf(this[1])
                    checkOperatorSearch(convertedKeywords)
                    return
                } else {
                    _error.value = SearchError.KEYWORD_SAME_NOT
                    return
                }
            }
            convertedKeywords = this
            checkOperatorSearch(convertedKeywords)
        }


    }
    fun checkOperatorSearch(keywords: Array<String>) {
        if (keywords.size == 1) {
            searchBooks(keywords[0], null)
            return
        }

        if (keywords[0].equals(STR_OPERATOR_OR)) {
            return
        }

        if (keywords[0].equals(STR_OPERATOR_NOT)) {
            searchBooks(keywords[1], keywords[2])
            return
        }
    }

    fun searchBooks(keword: String, excludedKeyword: String?) {
        searchRepository.searchBooks(
            keyword = keword,
            page = currentPage,
            onSuccess = { response ->
                if (response.total.toInt() == 0) {
                    _error.value = SearchError.RESPONSE_IS_NULL
                } else {
                    if (excludedKeyword != null) {
                        _searchedBooks.value = response.books.filter {
                            !it.title.lowercase().contains(excludedKeyword.lowercase())
                        }
                    } else {
                        _searchedBooks.value = response.books
                    }
                }
                isLoading = false
            },
            onError = {
                _error.value = SearchError.RESPONSE_IS_ERROR
                isLoading = false
            },
            onFailure = {
                _error.value = SearchError.NETWORK_FAILURE
                isLoading = false
            }
        )
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
                return arrayOf(STR_OPERATOR_OR, this[0] , this[1])
            }
        }

        if (keyword.contains(OPERATOR_NOT)) {
            with(keyword.split(OPERATOR_NOT)) {
                return arrayOf(STR_OPERATOR_NOT, this[0] , this[1])
            }
        }

        return arrayOf(keyword)
    }



    fun logd(msg: String) {
        Log.d(TAG, msg)
    }
}