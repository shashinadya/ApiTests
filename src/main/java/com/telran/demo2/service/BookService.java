package com.telran.demo2.service;

import com.telran.demo2.entity.Book;
import com.telran.demo2.exception.BookNotFoundException;
import com.telran.demo2.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("id = " + id));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void replaceBookNames(long id1, long id2) {
        Book book1 = getBook(id1);
        Book book2 = getBook(id2);

        String book1Name = book1.getName();
        String book2Name = book2.getName();

        book1.setName(book2Name);
        book2.setName(book1Name);

        saveBook(book1);
        saveBook(book2);
    }
}
