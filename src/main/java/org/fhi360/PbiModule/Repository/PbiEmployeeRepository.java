package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.PbiEmployee.PbiEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface PbiEmployeeRepository extends JpaRepository<PbiEmployee, UUID>, JpaSpecificationExecutor<PbiEmployee> {
    List<PbiEmployee> findAllByOrderByCreatedDateDesc();

    List<PbiEmployee> findByIdNumberOrPhoneNumberOrAccountNumber(String idNumber, String phoneNumber, String accountNumber);

    @Query("SELECT p FROM PbiEmployee p WHERE " +
            "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) AND " +
            " LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) OR " +
            "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) AND " +
            " LOWER(p.middleName) LIKE LOWER(CONCAT('%', :middleName, '%')) AND " +
            " LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) OR " +
            "(LOWER(p.middleName) LIKE LOWER(CONCAT('%', :middleName, '%')) AND " +
            " LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) OR " +
            "(LOWER(p.accountName) LIKE LOWER(CONCAT('%', :accountName, '%'))) OR " +
            "(LOWER(p.homeAddress) LIKE LOWER(CONCAT('%', :homeAddress, '%'))) OR " +
            "(LOWER(p.emailAddress) LIKE LOWER(CONCAT('%', :emailAddress, '%')))")
    List<PbiEmployee> findProbableMatches(String firstName, String middleName, String lastName,
                                          String accountName, String homeAddress, String emailAddress);

}
