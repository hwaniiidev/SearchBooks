package com.sinhwan.searchbooks.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.repository.SearchRepositoryImpl
import kotlinx.coroutines.*
import java.net.UnknownHostException

class SearchViewModel : ViewModel() {
    val TAG = this::class.java.simpleName

    var currentPage = 1
    var isLoading = false

    lateinit var convertedKeywords: Array<String>

    val searchKeyword = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
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
            _error.value = SearchError.KEYWORD_IS_NULL.errorMessage
            return
        }

        with(checkKeyword(searchValue)) {
            if (isNullOrEmpty()) {
                _error.value = SearchError.KEYWORD_OVER.errorMessage
                return
            }

            if (size > 1 && this[1].equals(this[2], true)) {
                if (this[0].equals(STR_OPERATOR_OR)) {
                    _error.value = "2개의 키워드가 일치합니다. ${getOnlyKeywordMessage(this[1])}"
                    convertedKeywords = arrayOf(this[1])
                    checkOperatorSearch(convertedKeywords)
                    return
                } else {
                    _error.value = SearchError.KEYWORD_SAME_NOT.errorMessage
                    return
                }
            }
            convertedKeywords = this
            checkOperatorSearch(convertedKeywords)
        }


    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        when (throwable) {
            is UnknownHostException -> _error.value = SearchError.NETWORK_FAILURE.errorMessage
            else -> _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
        }
    }

    fun checkOperatorSearch(keywords: Array<String>) {
        if (keywords.size == 1) {
            CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                val response = async {
                    searchRepository.searchBooks(keywords[0], currentPage)
                }.await()
                isLoading = false

                if (response.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                if (response.total.toInt() == 0) {
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }

                _searchedBooks.value = response.books
                currentPage++
            }
            return
        }

        if (keywords[0].equals(STR_OPERATOR_OR)) {
            CoroutineScope(Dispatchers.Main).launch {
                val aysncFirst = async {
                    searchRepository.searchBooks(keywords[1], currentPage)
                }
                val aysncSecond = async {
                    searchRepository.searchBooks(keywords[2], currentPage)
                }
                val responseFirst = aysncFirst.await()
                val responseSecond = aysncSecond.await()
                isLoading = false

                if (responseFirst.error != "0" || responseSecond.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                if (responseFirst.total.toInt() == 0 && responseSecond.total.toInt() == 0) {
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }
                
                if (responseFirst.total.toInt() == 0) {
                    _error.value = getNoneWithKeywordMessage(keywords[1], keywords[2])
                    convertedKeywords = arrayOf(keywords[2])
                }

                if (responseSecond.total.toInt() == 0) {
                    _error.value = getNoneWithKeywordMessage(keywords[2], keywords[1])
                    convertedKeywords = arrayOf(keywords[1])
                }

                val joined: MutableList<Book> = ArrayList()
                joined.addAll(responseFirst.books)
                joined.addAll(responseSecond.books)

                _searchedBooks.value = joined.distinct()
                currentPage++
            }
            return
        }

        if (keywords[0].equals(STR_OPERATOR_NOT)) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = async {
                    searchRepository.searchBooks(keywords[1], currentPage)
                }.await()
                isLoading = false

                if (response.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                if (response.total.toInt() == 0) {
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }

                _searchedBooks.value = response.books.filter {
                    !it.title.lowercase().contains(keywords[2].lowercase())
                }
                currentPage++
            }
            return
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
                return arrayOf(STR_OPERATOR_OR, this[0], this[1])
            }
        }

        if (keyword.contains(OPERATOR_NOT)) {
            with(keyword.split(OPERATOR_NOT)) {
                return arrayOf(STR_OPERATOR_NOT, this[0], this[1])
            }
        }

        return arrayOf(keyword)
    }

    private fun getNoneWithKeywordMessage(noneKeyword: String, withKeyword: String) : String {
        return "'$noneKeyword'의 검색 결과가 없습니다. ${getOnlyKeywordMessage(withKeyword)}"
    }

    private fun getOnlyKeywordMessage(keyword: String) : String {
        return "'$keyword'만 검색합니다."
    }

    fun logd(msg: String) {
        Log.d(TAG, msg)
    }
}