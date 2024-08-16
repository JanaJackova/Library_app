package com.library.libraryApp.service;

import com.library.libraryApp.dto.MemberDTO;

import java.util.List;

public interface MemberService {

    MemberDTO addMember(MemberDTO memberDTO);

    List<MemberDTO> getAllMembers();

    MemberDTO getMemberById(Long id);

    MemberDTO updateMember(MemberDTO memberDTO);

    void deleteMember(Long id);

    List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber);
}
