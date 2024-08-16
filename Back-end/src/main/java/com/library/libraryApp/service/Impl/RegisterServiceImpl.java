package com.library.libraryApp.service.Impl;

import com.library.libraryApp.dto.RegisterDTO;
import com.library.libraryApp.entity.CheckoutRegisterEntity;
import com.library.libraryApp.mapper.RegisterMapper;
import com.library.libraryApp.repository.CheckoutRegisterRepository;
import com.library.libraryApp.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    @Value("${library.loanPeriodInDays}")
    private int loanPeriodInDays;

    @Value("${library.overdueFineRate}")
    private double overdueFineRate;

    private final RegisterMapper registerMapper;
    private final CheckoutRegisterRepository checkoutRegisterRepository;

    @Override
    public RegisterDTO createRegister(RegisterDTO registerDTO) {
        CheckoutRegisterEntity checkoutRegister = registerMapper.mapToRegisterEntity(registerDTO);

        // calculation due date
        LocalDate dueDate = calculateDueDate(checkoutRegister.getCheckoutDate());
        checkoutRegister.setDueDate(dueDate);
        
        checkoutRegister = checkoutRegisterRepository.save(checkoutRegister);
        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public List<RegisterDTO> getAllRegister() {
        List<CheckoutRegisterEntity> checkoutRegisters = checkoutRegisterRepository.findAll();
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .toList();
    }

    @Override
    public RegisterDTO getRegisterById(Long id) {
        Optional<CheckoutRegisterEntity> checkoutRegisterOptional = checkoutRegisterRepository.findById(id);
        CheckoutRegisterEntity checkoutRegister = checkoutRegisterOptional.get();
        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public RegisterDTO updateRegister(RegisterDTO registerDTO) {
        if (registerDTO.getId() == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        Optional<CheckoutRegisterEntity> checkoutRegisterOptional = checkoutRegisterRepository.findById(registerDTO.getId());
        CheckoutRegisterEntity checkoutRegisterToUpdate = checkoutRegisterOptional.get();

        updateCheckoutRegisterFromDTO(checkoutRegisterToUpdate, registerDTO);
        calculateOverdueFine(checkoutRegisterToUpdate);

        CheckoutRegisterEntity updatedCheckoutRegister = checkoutRegisterRepository.save(checkoutRegisterToUpdate);
        return registerMapper.mapToRegisterDTO(updatedCheckoutRegister);
    }

    @Override
    public void deleteRegister(Long id) {
        checkoutRegisterRepository.deleteById(id);
    }

    @Override
    public List<RegisterDTO> getRegisterByMemberId(Long memberId) {
        List<CheckoutRegisterEntity> checkoutRegisters = checkoutRegisterRepository.findByMemberId(memberId);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .toList();
    }

    @Override
    public List<RegisterDTO> getRegisterByBookId(Long bookId) {
        List<CheckoutRegisterEntity> checkoutRegisters = checkoutRegisterRepository.findByBookId(bookId);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .toList();
    }

    private void calculateOverdueFine(CheckoutRegisterEntity checkoutRegister) {
        if (checkoutRegister.getReturnDate() != null &&
                checkoutRegister.getReturnDate().isAfter(checkoutRegister.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(checkoutRegister.getDueDate(), checkoutRegister.getReturnDate());
            double overdueFine = daysOverdue * overdueFineRate;
            checkoutRegister.setOverdueFine(overdueFine);
        }
    }

    private void updateCheckoutRegisterFromDTO(CheckoutRegisterEntity checkoutRegister, RegisterDTO registerDTO) {
        if (registerDTO.getDueDate() != null)
            checkoutRegister.setDueDate(LocalDate.parse(registerDTO.getDueDate()));
        if (registerDTO.getReturnDate() != null)
            checkoutRegister.setReturnDate(LocalDate.parse(registerDTO.getReturnDate()));
    }


    private LocalDate calculateDueDate(LocalDate checkoutDate) {
        return checkoutDate.plusDays(loanPeriodInDays);
    }
}
