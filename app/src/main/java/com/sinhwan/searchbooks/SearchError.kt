package com.sinhwan.searchbooks

enum class SearchError(val errorMessage: String) {
    KEYWORD_IS_NULL("키워드를 입려해주세요."),
    KEYWORD_OVER("키워드는 최대 2개까지 입력 가능합니다.")
}