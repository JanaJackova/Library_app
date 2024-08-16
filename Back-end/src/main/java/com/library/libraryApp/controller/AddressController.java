package com.library.libraryApp.controller;


import com.library.libraryApp.dto.AddressDTO;
import com.library.libraryApp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/addresses")
public class AddressController {

    private AddressService addressService;

    @PostMapping("createAddress")
    public ResponseEntity<AddressDTO> addAddress(@RequestBody  AddressDTO addressDTO) {
        AddressDTO savedAddress = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> allAddresses = addressService.getAllAddresses();
        return new ResponseEntity<>(allAddresses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AddressDTO> findAddressById(@PathVariable Long id) {
        AddressDTO addressDTO = addressService.getAddressById(id);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PatchMapping("updateAddress/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        addressDTO.setId(id);
        AddressDTO updateAddress = addressService.updateAddress(addressDTO);
        return new ResponseEntity<>(updateAddress, HttpStatus.OK);
    }

    @DeleteMapping("deleteAddress/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>("Address successfully deleted", HttpStatus.OK);
    }
}
