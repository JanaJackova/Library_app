package com.library.libraryApp.service.Impl;

import com.library.libraryApp.dto.AddressDTO;
import com.library.libraryApp.dto.MemberDTO;
import com.library.libraryApp.entity.AddressEntity;
import com.library.libraryApp.entity.MemberEntity;
import com.library.libraryApp.exception.ResourceNotFoundException;
import com.library.libraryApp.mapper.AddressMapper;
import com.library.libraryApp.mapper.MemberMapper;
import com.library.libraryApp.repository.AddressRepository;
import com.library.libraryApp.repository.MemberRepository;
import com.library.libraryApp.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;

    private AddressRepository addressRepository;

    private AddressServiceImpl addressService;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public MemberDTO addMember(MemberDTO memberDTO) {
        AddressDTO addressDTO = memberDTO.getAddress();

        AddressEntity address = Optional.ofNullable(addressDTO)
                .map(AddressMapper::mapToAddressEntity)
                .map(addressRepository::save)
                .orElse(null);

        MemberEntity member = MemberMapper.mapToMemberEntity(memberDTO);
        member.setAddress(address);

        member = memberRepository.save(member);
        return MemberMapper.mapToMemberDTO(member);
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> members = memberRepository.findAll();
        return members.stream()
                .map(MemberMapper::mapToMemberDTO)
                .toList();
    }


    @Override
    public MemberDTO getMemberById(Long id) {
        Optional<MemberEntity> optionalMember = memberRepository.findById(id);
        MemberEntity member = optionalMember.orElseThrow(
                () -> new ResourceNotFoundException("Member", "ID", id)
        );
        return MemberMapper.mapToMemberDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(MemberDTO memberDTO) {
        Optional<MemberEntity> optionalMember = memberRepository.findById(memberDTO.getId());

        MemberEntity memberToUpdate = optionalMember.orElseThrow(
                () -> new ResourceNotFoundException("Member", "ID", memberDTO.getId()));

        updateMemberEntityFromDTO(memberToUpdate, memberDTO);
        memberToUpdate = memberRepository.save(memberToUpdate);

        return MemberMapper.mapToMemberDTO(memberToUpdate);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", "ID", id);
        }
        memberRepository.deleteById(id);
    }

    @Override
    public List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MemberEntity> cq = cb.createQuery(MemberEntity.class);
        Root<MemberEntity> memberRoot = cq.from(MemberEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) predicates.add(cb.equal(memberRoot.get("id"), id));
        if (firstName != null && !firstName.isEmpty())
            predicates.add(cb.like(cb.lower(memberRoot.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        if (lastName != null && !lastName.isEmpty())
            predicates.add(cb.like(cb.lower(memberRoot.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        if (barcodeNumber != null && !barcodeNumber.isEmpty())
            predicates.add(cb.equal(cb.lower(memberRoot.get("barcodeNumber")), barcodeNumber.toLowerCase()));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<MemberEntity> result = entityManager.createQuery(cq).getResultList();
        return result.stream()
                .map(MemberMapper::mapToMemberDTO)
                .toList();
    }

    private void updateMemberEntityFromDTO(MemberEntity member, MemberDTO memberDTO) {
        if (memberDTO.getFirstName() != null) member.setFirstName(memberDTO.getFirstName());
        if (memberDTO.getLastName() != null) member.setLastName(memberDTO.getLastName());
        if (memberDTO.getDateOfBirth() != null) member.setDateOfBirth(LocalDate.parse(memberDTO.getDateOfBirth()));
        if (memberDTO.getEmail() != null) member.setEmail(memberDTO.getEmail());
        if (memberDTO.getPhone() != null) member.setPhone(memberDTO.getPhone());
        if (memberDTO.getMembershipStarted() != null) member.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted()));

        //the member is active if membershipEnded = null
        if (memberDTO.getMembershipEnded() != null) {
            if (memberDTO.getMembershipEnded().isEmpty()) {
                member.setMembershipEnded(null);
                member.setIsActive(true);
            } else {
                member.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));
                member.setIsActive(false);
            }
        }

        if (memberDTO.getBarcodeNumber() != null) member.setBarcodeNumber(memberDTO.getBarcodeNumber());

        if (memberDTO.getAddress() != null) {
            AddressEntity addressToUpdate;
            if (member.getAddress() != null) {
                addressToUpdate = member.getAddress();
            } else {
                addressToUpdate = new AddressEntity();
            }
            addressService.updateAddressEntityFromDTO(addressToUpdate, memberDTO.getAddress());
            addressRepository.save(addressToUpdate);
            member.setAddress(addressToUpdate);
        }
    }
}
