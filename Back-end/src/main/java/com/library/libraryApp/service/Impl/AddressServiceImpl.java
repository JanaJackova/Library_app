package com.library.libraryApp.service.Impl;

import com.library.libraryApp.dto.AddressDTO;
import com.library.libraryApp.entity.AddressEntity;
import com.library.libraryApp.mapper.AddressMapper;
import com.library.libraryApp.repository.AddressRepository;
import com.library.libraryApp.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {

    private AddressRepository addressRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        AddressEntity address = AddressMapper.mapToAddressEntity(addressDTO);
        address = addressRepository.save(address);
        return AddressMapper.mapToAddressDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<AddressEntity> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(AddressMapper::mapToAddressDTO)
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long id) {
        Optional<AddressEntity> optionalAddress = addressRepository.findById(id);
        AddressEntity address = optionalAddress.get();
        return AddressMapper.mapToAddressDTO(address);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(AddressDTO addressDTO) {
        Optional<AddressEntity> optionalAddress = addressRepository.findById(addressDTO.getId());
        AddressEntity addressToUpdate = optionalAddress.get();

        updateAddressEntityFromDTO(addressToUpdate, addressDTO);

        AddressEntity updatedAddress = addressRepository.save(addressToUpdate);

        return AddressMapper.mapToAddressDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    public void updateAddressEntityFromDTO(AddressEntity address, AddressDTO addressDTO) {
        if (addressDTO.getStreetName() != null) {
            address.setStreetName(addressDTO.getStreetName());
        }
        if (addressDTO.getStreetNumber() != null) {
            address.setStreetNumber(addressDTO.getStreetNumber());
        }
        if (addressDTO.getZipCode() != null) {
            address.setZipCode(addressDTO.getZipCode());
        }
        if (addressDTO.getPlaceName() != null) {
            address.setPlaceName(addressDTO.getPlaceName());
        }
        if (addressDTO.getCountry() != null) {
            address.setCountry(addressDTO.getCountry());
        }
        if (addressDTO.getAdditionalInfo() != null) {
            address.setAdditionalInfo(addressDTO.getAdditionalInfo());
        }
    }
}
