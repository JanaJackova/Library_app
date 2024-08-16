package com.library.libraryApp.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "books")
@Data
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private Integer yearOfPublication;

    @Column(nullable = false)
    private String placeOfPublication;

    @Column(nullable = false)
    private Integer noOfAvailableCopies;

    @Column(nullable = false)
    private String barcodeNumber;
}
