package com.library.libraryApp.controller;

import com.library.libraryApp.dto.MemberDTO;
import com.library.libraryApp.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private MemberService memberService;

    @PostMapping("addMember")
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO savedMember = memberService.addMember(memberDTO);
        return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> allMembers = memberService.getAllMembers();
        return new ResponseEntity<>(allMembers, HttpStatus.OK) ;
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return new ResponseEntity<>(member, HttpStatus.OK) ;
    }

    @PatchMapping("updateMember/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        memberDTO.setId(id);
        MemberDTO updatedMember = memberService.updateMember(memberDTO);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @DeleteMapping("deleteMember/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return new ResponseEntity<>("Member successfully deleted", HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<MemberDTO>> searchMembers(
            @RequestParam(required = false) Long cardNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String barcodeNumber) {
        List<MemberDTO> members = memberService.findMembersByCriteria(cardNumber, firstName, lastName, barcodeNumber);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

}
