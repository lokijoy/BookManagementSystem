package com.example.service

import com.example.db.tables.Authors
import com.example.exception.BadRequestException
import com.example.exception.DbAccessException
import com.example.exception.NotFoundException
import com.example.request.AuthorRequest
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class AuthorService(
    private val dsl: DSLContext
) {

    /**
     * 著者登録
     */
    fun createAuthor(request: AuthorRequest) {
        // 生年月日チェック
        val birth = LocalDate.parse(request.birth)
        if (!LocalDate.now().isAfter(birth)) { // 現在日付 > 生年月日 ではない場合
            throw BadRequestException("生年月日は本日以前の日付を設定してください。")
        }

        dsl.transactionResult { config ->
            val ctx = DSL.using(config)

            try {
                ctx.insertInto(Authors.AUTHORS)
                    .set(Authors.AUTHORS.LAST_NAME, request.lastName)
                    .set(Authors.AUTHORS.FIRST_NAME, request.firstName)
                    .set(Authors.AUTHORS.LAST_NAME_KANA, request.lastNameKana)
                    .set(Authors.AUTHORS.FIRST_NAME_KANA, request.firstNameKana)
                    .set(Authors.AUTHORS.BIRTH, LocalDate.parse(request.birth))
                    .set(Authors.AUTHORS.CREATED_BY, "system")
                    .set(Authors.AUTHORS.UPDATED_BY, "system")
                    .set(Authors.AUTHORS.CREATED_AT, LocalDateTime.now())
                    .set(Authors.AUTHORS.UPDATED_AT, LocalDateTime.now())
                    .execute()
            } catch (e: Exception) {
                throw DbAccessException("著者登録に失敗しました。エラー原因：${e.message}")
            }
        }
    }

    /**
     * 著者更新
     */
    fun updateAuthor(id: Int, request: AuthorRequest) {
        // 生年月日チェック
        val birth = LocalDate.parse(request.birth)
        if (!LocalDate.now().isAfter(birth)) { // 現在日付 > 生年月日 ではない場合
            throw BadRequestException("生年月日は本日以前の日付を設定してください。")
        }

        dsl.transactionResult { config ->
            val ctx = DSL.using(config)
            val rowsUpdated: Int

            try {
                rowsUpdated = ctx.update(Authors.AUTHORS)
                    .set(Authors.AUTHORS.LAST_NAME, request.lastName)
                    .set(Authors.AUTHORS.FIRST_NAME, request.firstName)
                    .set(Authors.AUTHORS.LAST_NAME_KANA, request.lastNameKana)
                    .set(Authors.AUTHORS.FIRST_NAME_KANA, request.firstNameKana)
                    .set(Authors.AUTHORS.BIRTH, LocalDate.parse(request.birth))
                    .set(Authors.AUTHORS.UPDATED_BY, "system")
                    .set(Authors.AUTHORS.UPDATED_AT, LocalDateTime.now())
                    .where(Authors.AUTHORS.ID.eq(id))
                    .execute()
            } catch (e: Exception) {
                throw DbAccessException("著者更新に失敗しました。エラー原因：${e.message}")
            }

            if (rowsUpdated == 0) {
                throw NotFoundException("著者が存在しませんでした。ID：${id}")
            }
        }
    }
}