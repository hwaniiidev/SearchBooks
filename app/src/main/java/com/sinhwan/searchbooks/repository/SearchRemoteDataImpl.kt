package com.sinhwan.searchbooks.repository

import com.sinhwan.searchbooks.ApiCall
import com.sinhwan.searchbooks.model.ResponseGetBooks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchRemoteDataImpl : SearchRemoteData{
    private val api = ApiCall.api

    override fun searchBooks(
        keyword: String,
        onSuccess: (response: ResponseGetBooks) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onFailure: (t: Throwable) -> Unit,
    ) {
        api.getSearchedBooks(keyword)
            .enqueue(object : Callback<ResponseGetBooks> {
                override fun onResponse(
                    call: Call<ResponseGetBooks>,
                    response: Response<ResponseGetBooks>,
                ) {
                    with(response) {
                        if (isSuccessful) {
                            body()?.let {
                                onSuccess(it)
                            }
                        } else {
                            onError(errorBody().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseGetBooks>, t: Throwable) {
                    onFailure(t)
                }

            })
    }

}