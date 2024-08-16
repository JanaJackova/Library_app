package com.library.libraryApp.service;

import com.library.libraryApp.dto.RegisterDTO;

import java.util.List;

public interface RegisterService {

    RegisterDTO createRegister(RegisterDTO registerDTO);

    List<RegisterDTO> getAllRegister();

    RegisterDTO getRegisterById(Long id);

    RegisterDTO updateRegister(RegisterDTO registerDTO);

    void deleteRegister(Long id);

    List<RegisterDTO> getRegisterByMemberId(Long memberId);
    List<RegisterDTO> getRegisterByBookId(Long bookId);
}
