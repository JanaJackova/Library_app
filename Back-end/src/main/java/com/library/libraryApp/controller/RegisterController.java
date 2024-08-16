package com.library.libraryApp.controller;


import com.library.libraryApp.dto.RegisterDTO;
import com.library.libraryApp.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/registers")
public class RegisterController {

    private RegisterService registerService;

    @PostMapping("createRegister")
    public ResponseEntity<RegisterDTO> createRegister(@RequestBody RegisterDTO registerDTO) {
        RegisterDTO register = registerService.createRegister(registerDTO);
        return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @GetMapping ("listAll")
    public ResponseEntity<List<RegisterDTO>> getAllRegisters() {
        List<RegisterDTO> registers= registerService.getAllRegister();
        return new ResponseEntity<List<RegisterDTO>>(registers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RegisterDTO> getRegisterById(@PathVariable Long id) {
        RegisterDTO register = registerService.getRegisterById(id);
        return new ResponseEntity<>(register, HttpStatus.OK);
    }

    @PatchMapping("updateRegister/{id}")
    public ResponseEntity<RegisterDTO> updateRegister(@PathVariable Long id, @RequestBody RegisterDTO registerDTO) {
        registerDTO.setId(id);
        RegisterDTO updatedRegister = registerService.updateRegister(registerDTO);
        return new ResponseEntity<>(updatedRegister, HttpStatus.OK);
    }

    @DeleteMapping("deleteRegister/{id}")
    public ResponseEntity<String> deleteRegister(@PathVariable Long id) {
        registerService.deleteRegister(id);
        return new ResponseEntity<>("Checkout register successfully deleted", HttpStatus.OK);
    }

    @GetMapping("member/{memberId}")
    public ResponseEntity<List<RegisterDTO>> getRegistersByMember(@PathVariable Long memberId) {
        List<RegisterDTO> registers = registerService.getRegisterByMemberId(memberId);
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }

    @GetMapping("book/{bookId}")
    public ResponseEntity<List<RegisterDTO>> getRegistersByBook(@PathVariable Long bookId) {
        List<RegisterDTO> registers = registerService.getRegisterByBookId(bookId);
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }
}
