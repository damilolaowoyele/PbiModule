package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.PbiPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PbiPaymentRepository extends JpaRepository<PbiPayment, UUID>, JpaSpecificationExecutor<PbiPayment> {
    //  Optional<PbiPayment> findTopByPbiEmployeeAndToDateBeforeOrderByToDateDesc(PbiEmployee employee, LocalDate date);
}
