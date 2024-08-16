package com.library.libraryApp.service;

import com.library.libraryApp.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long id);

    AddressDTO updateAddress(AddressDTO addressDTO);

    void deleteAddress(Long id);
}
