package com.library.libraryApp.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "checkout_register")
@Data
public class CheckoutRegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    private Double overdueFine;

}
