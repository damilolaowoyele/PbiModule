package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.PaymentSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentSettingRepository extends JpaRepository<PaymentSetting, UUID> {

    Optional<PaymentSetting> findByTeamType(String teamType);

    default Optional<PaymentSetting> getFixedCostForMobile() {
        return findByTeamType("mobile");
    }

    default Optional<PaymentSetting> getFixedCostForFacility() {
        return findByTeamType("facility");
    }
}


