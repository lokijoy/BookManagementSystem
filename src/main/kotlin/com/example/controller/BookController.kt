package com.example.controller

import com.example.request.BookRequest
import com.example.response.BookResponse
import com.example.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("books")
class BookController(
    private val bookService: BookService
) {

    /**
     * 書籍登録
     */
    @PostMapping
    fun createBook(@RequestBody request: BookRequest): ResponseEntity<String> {
        bookService.createBook(request)
        return ResponseEntity.ok("Book created successfully.")
    }

    /**
     * 書籍更新
     */
    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: Int, @RequestBody request: BookRequest): ResponseEntity<String> {
        bookService.updateBook(id, request)
        return ResponseEntity.ok("Book updated successfully.")
    }

    /**
     * 著者に紐づく書籍を取得
     */
    @GetMapping("/authors/{authorId}")
    fun getBooksByAuthor(@PathVariable authorId: Int): ResponseEntity<List<BookResponse>> {
        val books = bookService.getBooksByAuthor(authorId)
        return ResponseEntity.ok(books)
    }
}
