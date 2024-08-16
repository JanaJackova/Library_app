package com.library.libraryApp.service.Impl;

import com.library.libraryApp.controller.BookController;
import com.library.libraryApp.dto.BookDTO;
import com.library.libraryApp.entity.BookEntity;
import com.library.libraryApp.exception.ResourceNotFoundException;
import com.library.libraryApp.mapper.BookMapper;
import com.library.libraryApp.repository.BookRepository;
import com.library.libraryApp.service.BookService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        logger.info("Trying to add a book: {}", bookDTO);
        BookEntity book = BookMapper.mapToBookEntity(bookDTO);
        logger.info("Book entity after the mapping: {}", book);
        book = bookRepository.save(book);
        logger.info("The book successfully saved in database: {}", book);
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<BookEntity> books = bookRepository.findAll();
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .toList();
    }


    @Override
    public BookDTO getBookById(Long bookId) {
        //Optional<BookEntity> optionalBook = bookRepository.findById(bookId);
        //BookEntity book = optionalBook.get();
        BookEntity book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookId));
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    @Transactional
    public BookDTO updateBook(BookDTO bookDTO) {

        Optional<BookEntity> bookOptional = bookRepository.findById(bookDTO.getId());

        BookEntity bookToUpdate = bookOptional.orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookDTO.getId())
        );
        updateBookEntityFromDTO(bookToUpdate, bookDTO);

        BookEntity savedBook = bookRepository.save(bookToUpdate);
        return BookMapper.mapToBookDTO(savedBook);
    }

    @Override
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw  new ResourceNotFoundException("Book", "ID", bookId);
        }
        bookRepository.deleteById(bookId);
    }

    @Override
    public List<BookDTO> findBooksByTitle(String title) {
        List<BookEntity> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .toList();
    }

    @Override
    public List<BookDTO> findBooksByTitleAndAuthor(String title, String author) {
        List<BookEntity> books = bookRepository.findByTitleAndAuthorContainingIgnoreCase(title, author);
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .toList();
    }

    @Override
    public List<BookDTO> findBooksByCriteria(String title, String author, String isbn, String barcodeNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BookEntity> cq = cb.createQuery(BookEntity.class);
        Root<BookEntity> book = cq.from(BookEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (author != null && !author.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("author")), "%" + author.toLowerCase() + "%"));
        }
        if (isbn != null && !isbn.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("isbn")), "%" + isbn.toLowerCase() + "%"));
        }
        if (barcodeNumber != null && !barcodeNumber.isEmpty()) {
            predicates.add(cb.equal(cb.lower(book.get("barcodeNumber")), barcodeNumber.toLowerCase()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<BookEntity> result = entityManager.createQuery(cq).getResultList();
        return result.stream()
                .map(BookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    private void updateBookEntityFromDTO(BookEntity book, BookDTO bookDTO) {
        if (bookDTO.getTitle() != null) {
            book.setTitle(bookDTO.getTitle());
        }
        if (bookDTO.getAuthor() != null) {
            book.setAuthor(bookDTO.getAuthor());
        }
        if (bookDTO.getIsbn() != null) {
            book.setIsbn(bookDTO.getIsbn());
        }
        if (bookDTO.getPublisher() != null) {
            book.setPublisher(bookDTO.getPublisher());
        }
        if (bookDTO.getYearOfPublication() != null) {
            book.setYearOfPublication(bookDTO.getYearOfPublication());
        }
        if (bookDTO.getPlaceOfPublication() != null) {
            book.setPlaceOfPublication(bookDTO.getPlaceOfPublication());
        }
        if (bookDTO.getNoOfAvailableCopies() != null) {
            book.setNoOfAvailableCopies(bookDTO.getNoOfAvailableCopies());
        }
        if (bookDTO.getBarcodeNumber() != null) {
            book.setBarcodeNumber(bookDTO.getBarcodeNumber());
        }


    }

}
