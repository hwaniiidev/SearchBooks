package com.sinhwan.searchbooks.ui

import android.content.Context
import android.widget.Toast

const val KEY_DETAIL_BOOK = "detailBook"
const val INFINITE_SCROLL_FIRST_INDEX = 5
const val INFINITE_SCROLL_INDEX = 20

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}