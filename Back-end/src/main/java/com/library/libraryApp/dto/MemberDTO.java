package com.library.libraryApp.dto;

import lombok.Data;


@Data
public class MemberDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private AddressDTO address;
    private String email;
    private String phone;
    private String membershipStarted;
    private String membershipEnded;
    private Boolean isActive;
    private String barcodeNumber;
}
