package com.sinhwan.searchbooks.ui.search

enum class SearchError(val errorMessage: String) {
    KEYWORD_IS_NULL("키워드를 입려해주세요."),
    KEYWORD_OVER("키워드는 최대 2개까지 입력 가능합니다."),
    RESPONSE_IS_NULL("검색결과가 없습니다.\n다른 키워드로 검색해주세요."),
    RESPONSE_IS_ERROR("다시 시도해주세요"),
    NETWORK_FAILURE("네트워크 연결 상태를 확인하고 다시 시도해주세요.")
}