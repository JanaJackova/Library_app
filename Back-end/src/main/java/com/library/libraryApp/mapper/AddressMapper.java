package com.library.libraryApp.mapper;

import com.library.libraryApp.dto.AddressDTO;
import com.library.libraryApp.entity.AddressEntity;

public class AddressMapper {

    public static AddressDTO mapToAddressDTO(AddressEntity address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setStreetName(address.getStreetName());
        addressDTO.setStreetNumber(address.getStreetNumber());
        addressDTO.setZipCode(address.getZipCode());
        addressDTO.setPlaceName(address.getPlaceName());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setAdditionalInfo(address.getAdditionalInfo());
        return addressDTO;
    }

    public static AddressEntity mapToAddressEntity(AddressDTO addressDTO) {
        AddressEntity address = new AddressEntity();
        address.setId(addressDTO.getId());
        address.setStreetName(addressDTO.getStreetName());
        address.setStreetNumber(addressDTO.getStreetNumber());
        address.setZipCode(addressDTO.getZipCode());
        address.setPlaceName(addressDTO.getPlaceName());
        address.setCountry(addressDTO.getCountry());
        address.setAdditionalInfo(addressDTO.getAdditionalInfo());
        return address;

    }




}
