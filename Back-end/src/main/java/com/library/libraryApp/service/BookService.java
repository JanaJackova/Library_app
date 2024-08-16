package com.library.libraryApp.service;

import com.library.libraryApp.dto.BookDTO;
import com.library.libraryApp.entity.BookEntity;

import java.util.List;


public interface BookService {

    BookDTO addBook(BookDTO bookDTO);

    List<BookDTO> getAllBooks();

    BookDTO getBookById(Long bookId);

    BookDTO updateBook(BookDTO bookDTO);

    void deleteBook(Long bookId);

    List<BookDTO> findBooksByTitle(String title);

    List<BookDTO> findBooksByTitleAndAuthor(String title, String author);

    List<BookDTO> findBooksByCriteria(String title, String author, String isbn, String barcodeNumber);
}
