package com.example.service

import com.example.db.tables.Books
import com.example.db.tables.BookAuthorMapping
import com.example.exception.BadRequestException
import com.example.exception.DbAccessException
import com.example.exception.NotFoundException
import com.example.request.BookRequest
import com.example.response.BookResponse
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookService(
    private val dsl: DSLContext
) {

    /**
     * 書籍登録
     */
    fun createBook(request: BookRequest) {
        // 著者チェック
        if (request.authorIds.isEmpty()) {
            throw BadRequestException("最低1人の著者を設定してください。")
        }

        dsl.transactionResult { config ->
            val ctx = DSL.using(config)

            try {
                // 書籍の登録
                val bookId = ctx.insertInto(Books.BOOKS)
                    .set(Books.BOOKS.NAME, request.name)
                    .set(Books.BOOKS.PRICE, request.price)
                    .set(Books.BOOKS.STATUS, request.status)
                    .set(Books.BOOKS.CREATED_BY, "system")
                    .set(Books.BOOKS.UPDATED_BY, "system")
                    .set(Books.BOOKS.CREATED_AT, LocalDateTime.now())
                    .set(Books.BOOKS.UPDATED_AT, LocalDateTime.now())
                    .returning(Books.BOOKS.ID)
                    .fetchOne()!!.id

                // 書籍と著者のマッピング登録
                request.authorIds.forEach { authorId ->
                    ctx.insertInto(BookAuthorMapping.BOOK_AUTHOR_MAPPING)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.BOOK_ID, bookId)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.AUTHOR_ID, authorId)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.CREATED_BY, "system")
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.UPDATED_BY, "system")
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.CREATED_AT, LocalDateTime.now())
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.UPDATED_AT, LocalDateTime.now())
                        .execute()
                }
            } catch (e: Exception) {
                throw DbAccessException("書籍登録に失敗しました。エラー原因：${e.message}")
            }
        }
    }

    /**
     * 書籍更新
     */
    fun updateBook(id: Int, request: BookRequest) {
        // 著者チェック
        if (request.authorIds.isEmpty()) {
            throw BadRequestException("最低1人の著者を設定してください。")
        }

        dsl.transaction { config ->
            val ctx = DSL.using(config)

            // 書籍のステータスチェック
            if (request.status == 0) {
                // 書籍の取得
                val record = dsl.select(Books.BOOKS.STATUS)
                    .from(Books.BOOKS)
                    .where(Books.BOOKS.ID.eq(id))
                    .fetchOne()
                if (record != null && record.getValue(Books.BOOKS.STATUS) == 9) {
                    throw BadRequestException("出版済みの書籍を未出版に更新できません。")
                }
            }

            // 書籍情報を更新
            val rowsUpdated: Int
            try {
                rowsUpdated = ctx.update(Books.BOOKS)
                    .set(Books.BOOKS.NAME, request.name)
                    .set(Books.BOOKS.PRICE, request.price)
                    .set(Books.BOOKS.STATUS, request.status)
                    .set(Books.BOOKS.UPDATED_BY, "system")
                    .set(Books.BOOKS.UPDATED_AT, LocalDateTime.now())
                    .where(Books.BOOKS.ID.eq(id))
                    .execute()
            } catch (e: Exception) {
                throw DbAccessException("書籍更新に失敗しました。エラー原因：${e.message}")
            }

            if (rowsUpdated == 0) {
                throw NotFoundException("書籍が存在しませんでした。ID：${id}")
            }

            try {
                // 既存のマッピングを削除
                ctx.deleteFrom(BookAuthorMapping.BOOK_AUTHOR_MAPPING)
                    .where(BookAuthorMapping.BOOK_AUTHOR_MAPPING.BOOK_ID.eq(id))
                    .execute()

                // 新しいマッピングを登録
                request.authorIds.forEach { authorId ->
                    ctx.insertInto(BookAuthorMapping.BOOK_AUTHOR_MAPPING)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.BOOK_ID, id)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.AUTHOR_ID, authorId)
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.CREATED_BY, "system")
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.UPDATED_BY, "system")
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.CREATED_AT, LocalDateTime.now())
                        .set(BookAuthorMapping.BOOK_AUTHOR_MAPPING.UPDATED_AT, LocalDateTime.now())
                        .execute()
                }
            } catch (e: Exception) {
                throw DbAccessException("書籍著者マッピングの更新に失敗しました。エラー原因：${e.message}")
            }
        }
    }

    /**
     * 著者に紐づく書籍を取得
     */
    fun getBooksByAuthor(authorId: Int): List<BookResponse> {
        val books = dsl.select(Books.BOOKS.ID, Books.BOOKS.NAME, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .from(Books.BOOKS)
            .join(BookAuthorMapping.BOOK_AUTHOR_MAPPING)
            .on(Books.BOOKS.ID.eq(BookAuthorMapping.BOOK_AUTHOR_MAPPING.BOOK_ID))
            .where(BookAuthorMapping.BOOK_AUTHOR_MAPPING.AUTHOR_ID.eq(authorId))
            .fetch()

        if (books.isEmpty()) {
            return emptyList()
        }

        return books.map { record ->
            BookResponse(
                bookId = record.get(Books.BOOKS.ID)!!, // NOT NULL項目だからNULLはあり得ない
                name = record.get(Books.BOOKS.NAME)!!, // NOT NULL項目だからNULLはあり得ない
                price = record.get(Books.BOOKS.PRICE)!!, // NOT NULL項目だからNULLはあり得ない
                status = record.get(Books.BOOKS.STATUS)!! // NOT NULL項目だからNULLはあり得ない
            )
        }
    }
}
