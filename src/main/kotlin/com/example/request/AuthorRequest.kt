package com.example.request

data class AuthorRequest(
    /* ID。nullの場合、新規作成を意味する */
    val id: Int?,
    /* 氏名（姓） */
    val lastName: String,
    /* 氏名（名） */
    val firstName: String,
    /* 氏名カナ（姓） */
    val lastNameKana: String,
    /* 氏名カナ（名） */
    val firstNameKana: String,
    /* 生年月日（文字列形式） */
    val birth: String
)
