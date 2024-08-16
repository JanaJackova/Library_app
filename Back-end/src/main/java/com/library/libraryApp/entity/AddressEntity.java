package com.library.libraryApp.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "addresses")
@Data
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String streetName;

    @Column(nullable = false)
    private String streetNumber;

    private String zipCode;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String country;

    private String additionalInfo;

}
