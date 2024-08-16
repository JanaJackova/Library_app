package com.library.libraryApp.dto;


import lombok.Data;


@Data
public class RegisterDTO {

    private Long id;
    private Long memberId;
    private Long bookId;
    private String checkoutDate;
    private String dueDate;
    private String returnDate;
    private Double overdueFine;
}
