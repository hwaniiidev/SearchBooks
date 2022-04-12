package com.sinhwan.searchbooks

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
        Assert.assertArrayEquals(arrayOf(), SearchViewModel().checkKeyword("java-kotlin|python"))
        Assert.assertArrayEquals(arrayOf(), SearchViewModel().checkKeyword("java|kotlin-python"))
    }
}