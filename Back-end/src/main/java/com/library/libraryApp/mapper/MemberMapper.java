package com.library.libraryApp.mapper;

import com.library.libraryApp.dto.MemberDTO;
import com.library.libraryApp.entity.MemberEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MemberMapper {

    public static MemberDTO mapToMemberDTO(MemberEntity member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setFirstName(member.getFirstName());
        memberDTO.setLastName(member.getLastName());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (member.getDateOfBirth() != null) {
            memberDTO.setDateOfBirth(member.getDateOfBirth().format(formatter));
        }

        if (member.getAddress() != null) {
            memberDTO.setAddress(AddressMapper.mapToAddressDTO(member.getAddress()));
        }

        memberDTO.setEmail(member.getEmail());
        memberDTO.setPhone(member.getPhone());

        if (member.getMembershipStarted() != null) {
            memberDTO.setMembershipStarted((member.getMembershipStarted().format(formatter)));
        }

        if (member.getMembershipEnded() != null) {
            memberDTO.setMembershipEnded((member.getMembershipEnded().format(formatter)));
        }

        memberDTO.setIsActive(member.getIsActive());
        memberDTO.setBarcodeNumber(member.getBarcodeNumber());
        return memberDTO;
    }

    public static MemberEntity mapToMemberEntity(MemberDTO memberDTO) {
        MemberEntity member = new MemberEntity();
        member.setId(memberDTO.getId());
        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());

        member.setDateOfBirth(LocalDate.parse(memberDTO.getDateOfBirth()));

        if (memberDTO.getAddress() != null) {
            member.setAddress(AddressMapper.mapToAddressEntity(memberDTO.getAddress()));
        }

        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());
        member.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted()));

        if (memberDTO.getMembershipEnded() != null) {
            member.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));
        }

        member.setIsActive(memberDTO.getIsActive());
        member.setBarcodeNumber(memberDTO.getBarcodeNumber());

        return member;
    }
}
