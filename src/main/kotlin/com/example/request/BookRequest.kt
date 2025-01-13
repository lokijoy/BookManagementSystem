package com.example.request

data class BookRequest(
    /* 書籍名 */
    val name: String,
    /* 価格 */
    val price: Int,
    /* ステータス */
    val status: Int,
    /* 著者IDリスト */
    val authorIds: List<Int>
)
