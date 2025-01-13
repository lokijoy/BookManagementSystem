package com.example.controller

import com.example.request.AuthorRequest
import com.example.service.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    /**
     * 著者登録
     */
    @PostMapping
    fun createAuthor(@RequestBody request: AuthorRequest): ResponseEntity<String> {
        authorService.createAuthor(request)
        return ResponseEntity.ok("Author created successfully.")
    }

    /**
     * 著者更新
     */
    @PutMapping("/{id}")
    fun updateAuthor(@PathVariable id: Int, @RequestBody request: AuthorRequest): ResponseEntity<String> {
        authorService.updateAuthor(id, request)
        return ResponseEntity.ok("Author updated successfully.")
    }
}
