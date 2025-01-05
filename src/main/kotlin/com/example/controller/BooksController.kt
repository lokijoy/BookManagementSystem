package com.example.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/books")
class BooksController {

    @GetMapping("author")
    fun index(@RequestParam last_name: String, first_name: String) :String {
        return "";
    }
}