package com.sinhwan.searchbooks.ui.detailbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sinhwan.searchbooks.R
import com.sinhwan.searchbooks.databinding.ActivityDetailBookBinding
import com.sinhwan.searchbooks.model.Book
import com.sinhwan.searchbooks.ui.KEY_DETAIL_BOOK
import com.sinhwan.searchbooks.ui.search.SearchActivity
import com.sinhwan.searchbooks.ui.toast

class DetailBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(intent.extras) {
            if (this != null) {
                binding = DataBindingUtil.setContentView(this@DetailBookActivity, R.layout.activity_detail_book)
                binding.book = intent.extras?.get(KEY_DETAIL_BOOK) as Book
            } else {
                toast("선택하신 책 정보를 찾을 수 없습니다. 다시 시도해주세요.")
                finish()
            }
        }
    }
}