package com.library.libraryApp.dto;

import lombok.Data;

@Data
public class AddressDTO {

    private Long id;
    private String streetName;
    private String streetNumber;
    private String zipCode;
    private String placeName;
    private String country;
    private String additionalInfo;
}
