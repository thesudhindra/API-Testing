package com.playground.enterprise.loan.repository;

import com.playground.enterprise.loan.entity.LoanRepaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepaymentEntity, String> {

    Optional<LoanRepaymentEntity> findByIdAndTenantId(String id, String tenantId);

    Page<LoanRepaymentEntity> findByTenantIdAndLoanId(String tenantId, String loanId, Pageable pageable);
}
