package com.sinhwan.searchbooks

import com.sinhwan.searchbooks.ui.search.SearchError
import com.sinhwan.searchbooks.ui.search.SearchViewModel
import org.junit.Assert
import org.junit.Test

class SearchBooksUnitTest {
    @Test
    fun checked_keyword_isCorrect() {
        val OPERATOR_OR = "|"
        val OPERATOR_NOT = "-"
        Assert.assertArrayEquals(arrayOf("java"), SearchViewModel().checkKeyword("java"))
        Assert.assertArrayEquals(arrayOf("kotlin"), SearchViewModel().checkKeyword("kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("java|kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("java-kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("java|kotlin|"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("java|kotlin-"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("java-kotlin-"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("java-kotlin|"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("|java|kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("-java|kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("-java-kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("|java-kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("|java|kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_OR, "java", "kotlin"), SearchViewModel().checkKeyword("-java|kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("-java-kotlin"))
        Assert.assertArrayEquals(arrayOf(OPERATOR_NOT, "java", "kotlin"), SearchViewModel().checkKeyword("|java-kotlin"))

    }

    @Test
    fun checked_keyword_exception() {
        try {
            SearchViewModel().checkKeyword("java|kotlin-python")
        } catch (e: Throwable) {
            Assert.assertEquals(SearchError.KEYWORD_OVER.errorMessage, e.message)
        }

        try {
            SearchViewModel().checkKeyword(" |  ")
        } catch (e: Throwable) {
            Assert.assertEquals(SearchError.KEYWORD_IS_BLANK.errorMessage, e.message)
        }

        try {
            SearchViewModel().checkKeyword("java-java")
        } catch (e: Throwable) {
            Assert.assertEquals(SearchError.KEYWORD_SAME_NOT.errorMessage, e.message)
        }

        try {
            SearchViewModel().checkKeyword(" -java")
        } catch (e: Throwable) {
            Assert.assertEquals(SearchError.KEYWORD_IS_BLANK.errorMessage, e.message)
        }

        try {
            SearchViewModel().checkKeyword(" -  ")
        } catch (e: Throwable) {
            Assert.assertEquals(SearchError.KEYWORD_IS_BLANK.errorMessage, e.message)
        }
    }
}