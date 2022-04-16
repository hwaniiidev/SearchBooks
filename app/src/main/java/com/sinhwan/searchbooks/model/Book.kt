package com.sinhwan.searchbooks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book (
    val image: String,
    val isbn13: String,
    val price: String,
    val subtitle: String,
    val title: String,
    val url: String
) : Parcelable
