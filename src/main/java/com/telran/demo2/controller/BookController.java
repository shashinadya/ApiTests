package com.telran.demo2.controller;

import com.telran.demo2.entity.Book;
import com.telran.demo2.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/books")
    public Book createBook(@RequestBody @Valid Book book) {
        return bookService.saveBook(book);
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable @Positive long id) {
        return bookService.getBook(id);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks(@RequestParam(required = false) List<String> author,
                                  @RequestParam(required = false) String year,
                                  @RequestParam(required = false) String sort) {
        return bookService.getAllBooks();
    }

    //to show transactional functionality
    @PutMapping("/books")
    public void replaceBookNames(@RequestParam @Positive long id1,
                                 @RequestParam @Positive long id2) {
        bookService.replaceBookNames(id1, id2);
    }
}
