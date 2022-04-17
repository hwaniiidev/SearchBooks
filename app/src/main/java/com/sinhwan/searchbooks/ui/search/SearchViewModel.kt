package com.sinhwan.searchbooks.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.repository.SearchRepositoryImpl
import com.sinhwan.searchbooks.ui.INFINITE_SCROLL_FIRST_INDEX
import com.sinhwan.searchbooks.ui.INFINITE_SCROLL_INDEX
import kotlinx.coroutines.*
import java.net.UnknownHostException

class SearchViewModel : ViewModel() {

    var currentPage = 1
    var isFirstRequest = true

    // 검색 결과에 따라 infinite scroll 시 검색 query가 달라질 수 있음. 이를 위해 전역 변수로 관리.
    lateinit var convertedKeywords: Array<String>

    val searchKeyword = MutableLiveData<String>()

    // 검색 결과 마지막 페이지일 경우 더 이상 infinite scroll을 안하기 위해 마지막 페이지 여부 저장.
    private var _isPageOver = MutableLiveData<Boolean>()
    val isPageOver: LiveData<Boolean> = _isPageOver

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hideKeyboard = MutableLiveData<Unit>()
    val hideKeyboard: LiveData<Unit> = _hideKeyboard

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
            // infinite scroll을 더욱 부드럽게(빠르게 paging)을 원한다면 INFINITE_SCROLL_INDEX을 더 높여야함.
            val srollIndex = if (isFirstRequest) INFINITE_SCROLL_FIRST_INDEX else INFINITE_SCROLL_INDEX
            with((view.layoutManager!!) as LinearLayoutManager) {
                if (!isLoading.value!! &&
                    !isPageOver.value!! &&
                    findLastCompletelyVisibleItemPosition() > (itemCount - srollIndex)) {
                    checkOperatorSearch(convertedKeywords)
                    isFirstRequest = false
                }
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        when (throwable) {
            is UnknownHostException -> _error.value = SearchError.NETWORK_FAILURE.errorMessage
            else -> _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
        }
    }

    fun onClickSearchButton() {
        isFirstRequest = true
        _isPageOver.value = false
        _searchedBooks.value = null
        currentPage = 1
        val searchValue = searchKeyword.value
        if (searchValue.isNullOrBlank()) {
            _error.value = SearchError.KEYWORD_IS_NULL.errorMessage
            return
        }

        try {
            with(checkKeyword(searchValue)) {
                convertedKeywords = this
                checkOperatorSearch(convertedKeywords)
            }
        } catch (e: Throwable) {
            _error.value = e.message
        }
    }



    fun checkOperatorSearch(keywords: Array<String>) {
        _isLoading.value = true
        _hideKeyboard.value = Unit

        if (keywords.size == 1) {
            CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                val response = async {
                    searchRepository.searchBooks(keywords[0], currentPage)
                }.await()
                _isLoading.value = false

                if (response.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                if (response.total.toInt() == 0) {
                    if (currentPage > 1) {
                        _isPageOver.value = true
                        _error.value = SearchError.PAGE_IS_OVER.errorMessage
                        return@launch
                    }
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }

                _searchedBooks.value = response.books
                currentPage++
            }
            return
        }

        if (keywords[0] == STR_OPERATOR_OR) {
            CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                val aysncFirst = async {
                    searchRepository.searchBooks(keywords[1], currentPage)
                }
                val aysncSecond = async {
                    searchRepository.searchBooks(keywords[2], currentPage)
                }
                val responseFirst = aysncFirst.await()
                val responseSecond = aysncSecond.await()
                _isLoading.value = false

                if (responseFirst.error != "0" || responseSecond.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                if (responseFirst.total.toInt() == 0 && responseSecond.total.toInt() == 0) {
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }

                if (responseFirst.total.toInt() == 0) {
                    if (currentPage > 1) {
                        _error.value = getNoneWithKeywordMessage(keywords[1], keywords[2], true)
                    }
                    _error.value = getNoneWithKeywordMessage(keywords[1], keywords[2], false)

                    // keywords[1]의 검색 결과가 더 이상 없으므로 keywords[2] 만 검색
                    convertedKeywords = arrayOf(keywords[2])
                }

                if (responseSecond.total.toInt() == 0) {
                    if (currentPage > 1) {
                        _error.value = getNoneWithKeywordMessage(keywords[1], keywords[2], true)
                    }
                    _error.value = getNoneWithKeywordMessage(keywords[2], keywords[1], false)

                    // keywords[2]의 검색 결과가 더 이상 없으므로 keywords[1] 만 검색
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

        if (keywords[0] == STR_OPERATOR_NOT) {
            CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                val response = async {
                    searchRepository.searchBooks(keywords[1], currentPage)
                }.await()
                _isLoading.value = false

                if (response.error != "0") {
                    _error.value = SearchError.RESPONSE_IS_ERROR.errorMessage
                    return@launch
                }

                var filtered : List<Book>? = null
                var isResultEmpty = false
                if (response.total.toInt() == 0) {
                    isResultEmpty = true
                } else {
                    filtered = response.books.filter {
                        !it.title.lowercase().contains(keywords[2].lowercase())
                    }
                    if (filtered.isEmpty()) {
                        isResultEmpty = true
                    }
                }

                if (isResultEmpty) {
                    if (currentPage > 1) {
                        _isPageOver.value = true
                        _error.value = SearchError.PAGE_IS_OVER.errorMessage
                        return@launch
                    }
                    _error.value = SearchError.RESPONSE_IS_NULL.errorMessage
                    return@launch
                }

                _searchedBooks.value = filtered!!
                currentPage++
            }
            return
        }
    }

    /**
     * @param searchValue 사용자가 입력한 키워드
     *
     * @return 전처리된 검색 키워드
     * size 가 1 인 경우 operator가 포함되지 않은 경우
     * size 가 1이 아닌 경우 array[0]가 operator이며
     * array[1]가 operator 이전 키워드
     */
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
            throw Error(SearchError.KEYWORD_OVER.errorMessage)
        }

        if (keyword.contains(OPERATOR_OR)) {
            with(keyword.split(OPERATOR_OR)) {
                if (this[0].isBlank() && this[1].isBlank()) {
                    throw Error(SearchError.KEYWORD_IS_BLANK.errorMessage)
                }

                if (this[0].isBlank()) {
                    _error.value = SearchError.KEYWORD_CONTAIN_BLANK.errorMessage
                    return arrayOf(this[1])
                }

                if (this[1].isBlank()) {
                    _error.value = SearchError.KEYWORD_CONTAIN_BLANK.errorMessage
                    return arrayOf(this[0])
                }

                if (this[0].equals(this[1], true)) {
                    _error.value = "2개의 키워드가 일치합니다. ${getOnlyKeywordMessage(this[0])}"
                    return arrayOf(this[0])
                }

                return arrayOf(STR_OPERATOR_OR, this[0], this[1])
            }
        }

        if (keyword.contains(OPERATOR_NOT)) {
            with(keyword.split(OPERATOR_NOT)) {
                if (this[0].isBlank()) {
                    throw Error(SearchError.KEYWORD_IS_BLANK.errorMessage)
                }

                if (this[1].isBlank()) {
                    _error.value = SearchError.KEYWORD_CONTAIN_BLANK.errorMessage
                    return arrayOf(this[0])
                }

                if (this[0].equals(this[1], true)) {
                    throw Error(SearchError.KEYWORD_SAME_NOT.errorMessage)
                }
                return arrayOf(STR_OPERATOR_NOT, this[0], this[1])
            }
        }

        if (keyword.isBlank()) {
            throw Error(SearchError.KEYWORD_IS_BLANK.errorMessage)
        }

        return arrayOf(keyword)
    }

    private fun getNoneWithKeywordMessage(
        noneKeyword: String,
        withKeyword: String,
        noMore: Boolean
    ) : String {
        val strNoMore = if (noMore) " 더 이상" else ""
        return "'$noneKeyword'의 검색 결과가$strNoMore 없습니다. ${getOnlyKeywordMessage(withKeyword)}"
    }

    private fun getOnlyKeywordMessage(keyword: String) : String {
        return "'$keyword'만 검색합니다."
    }
}