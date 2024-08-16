package com.library.libraryApp.repository;

import com.library.libraryApp.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByTitleContainingIgnoreCase(String title);

    List<BookEntity> findByTitleAndAuthorContainingIgnoreCase(String title, String author);
}
