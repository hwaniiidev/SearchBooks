package com.sinhwan.searchbooks.ui

import android.content.Context
import android.widget.Toast

const val KEY_DETAIL_BOOK = "detailBook"

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}