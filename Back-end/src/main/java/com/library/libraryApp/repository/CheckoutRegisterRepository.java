package com.library.libraryApp.repository;

import com.library.libraryApp.entity.CheckoutRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutRegisterRepository extends JpaRepository<CheckoutRegisterEntity, Long> {

   List<CheckoutRegisterEntity> findByMemberId(Long memberId);

   List<CheckoutRegisterEntity> findByBookId(Long bookId);
}
