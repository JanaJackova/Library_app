package com.library.libraryApp.controller;


import com.library.libraryApp.dto.BookDTO;
import com.library.libraryApp.service.BookService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/books")
@AllArgsConstructor
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger((BookController.class));

    private BookService bookService;

   @PostMapping("addBook")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
       logger.info("Adding a book...");
       BookDTO savedBookDTO = bookService.addBook(bookDTO);
       return new ResponseEntity<>(savedBookDTO, HttpStatus.CREATED);
   }

   @GetMapping("listAll")
    public ResponseEntity<List<BookDTO>>getAllBooks() {
        List<BookDTO> allBooks = bookService.getAllBooks();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
   }

   @GetMapping("{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("id") Long bookId) {
       BookDTO bookDTO = bookService.getBookById(bookId);
       return new ResponseEntity<>(bookDTO, HttpStatus.OK);
   }

   @PatchMapping("updateBook/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
       bookDTO.setId(id);
       BookDTO updatedBook = bookService.updateBook(bookDTO);
       return new ResponseEntity<>(updatedBook, HttpStatus.OK);
   }

   @DeleteMapping("deleteBook/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
       bookService.deleteBook(id);
       return new ResponseEntity<>("Book successfully deleted.", HttpStatus.OK);
   }

   @GetMapping("search-title")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String title) {
       List<BookDTO> books = bookService.findBooksByTitle(title);
       return new ResponseEntity<>(books, HttpStatus.OK);
   }

    @GetMapping("search-title-author")
    public ResponseEntity<List<BookDTO>> searchBooksByTitleAndAuthor(@RequestParam String title,
                                                                     @RequestParam String author) {
        List<BookDTO> books = bookService.findBooksByTitleAndAuthor(title, author);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String barcodeNumber) {
        List<BookDTO> books = bookService.findBooksByCriteria(title, author, isbn, barcodeNumber);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
