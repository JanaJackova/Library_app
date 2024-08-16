package com.library.libraryApp.mapper;

import com.library.libraryApp.dto.RegisterDTO;
import com.library.libraryApp.entity.BookEntity;
import com.library.libraryApp.entity.CheckoutRegisterEntity;
import com.library.libraryApp.entity.MemberEntity;
import com.library.libraryApp.repository.BookRepository;
import com.library.libraryApp.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Component
public class RegisterMapper {

    private MemberRepository memberRepository;
    private BookRepository bookRepository;

    public RegisterDTO mapToRegisterDTO(CheckoutRegisterEntity checkoutRegister) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setId(checkoutRegister.getId());
        registerDTO.setMemberId(checkoutRegister.getMember().getId());
        registerDTO.setBookId(checkoutRegister.getBook().getId());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        registerDTO.setCheckoutDate(checkoutRegister.getCheckoutDate().format(formatter));
        registerDTO.setDueDate(checkoutRegister.getDueDate().format(formatter));
        if (checkoutRegister.getReturnDate() != null)
            registerDTO.setReturnDate(checkoutRegister.getReturnDate().format(formatter));

        registerDTO.setOverdueFine(checkoutRegister.getOverdueFine());
        return registerDTO;
    }

    public CheckoutRegisterEntity mapToRegisterEntity(RegisterDTO registerDTO) {
        CheckoutRegisterEntity checkoutRegister = new CheckoutRegisterEntity();
        checkoutRegister.setId(registerDTO.getId());

        MemberEntity member = memberRepository.findById(registerDTO.getMemberId()).get();
        checkoutRegister.setMember(member);

        BookEntity book = bookRepository.findById(registerDTO.getBookId()).get();
        checkoutRegister.setBook(book);

        checkoutRegister.setCheckoutDate(LocalDate.parse(registerDTO.getCheckoutDate()));

        if (registerDTO.getDueDate() != null)
            checkoutRegister.setDueDate(LocalDate.parse(registerDTO.getDueDate()));

        if (registerDTO.getReturnDate() != null)
            checkoutRegister.setReturnDate(LocalDate.parse(registerDTO.getReturnDate()));

        checkoutRegister.setOverdueFine(registerDTO.getOverdueFine());

        return  checkoutRegister;
    }

}
