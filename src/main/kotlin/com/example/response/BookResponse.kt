package com.example.response

data class BookResponse(
    /* ID */
    val bookId: Int,
    /* 書籍名 */
    val name: String,
    /* 価格 */
    val price: Int,
    /* ステータス */
    val status: Int,
)
