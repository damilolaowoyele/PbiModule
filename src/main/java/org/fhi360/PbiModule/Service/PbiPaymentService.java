package org.fhi360.PbiModule.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Exception.CustomConstraintViolationException;
import org.fhi360.PbiModule.Exception.ResourceNotFoundException;
import org.fhi360.PbiModule.Model.PbiPayment;
import org.fhi360.PbiModule.Repository.PaymentSettingRepository;
import org.fhi360.PbiModule.Repository.PbiPaymentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PbiPaymentService {

    private final PbiPaymentRepository pbiPaymentRepository;
    private final PaymentSettingRepository paymentSettingRepository;
    private final Validator validator;

    public PbiPayment savePbiPayment(PbiPayment pbiPayment) {

        Set<ConstraintViolation<PbiPayment>> violations = validator.validate(pbiPayment);
        if (!violations.isEmpty()) {
            throw new CustomConstraintViolationException("Input validation failed", violations);
        }

        return pbiPaymentRepository.save(pbiPayment);
    }

    public List<PbiPayment> saveAllPbiPayments(List<PbiPayment> pbiPayments) {
        return pbiPaymentRepository.saveAll(pbiPayments);
    }

    public PbiPayment getPbiPaymentById(UUID id) {
        return pbiPaymentRepository.findById(id).orElse(null);
    }

    public Page<PbiPayment> getPbiPayments(Pageable pageable, String searchText) {
        String lowerCaseSearchText = searchText.toLowerCase();

        if (lowerCaseSearchText.isEmpty()) {
            return pbiPaymentRepository.findAll(pageable);
        }

        Specification<PbiPayment> spec = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("state")), "%" + lowerCaseSearchText + "%"),
                cb.like(cb.lower(root.get("lga")), "%" + lowerCaseSearchText + "%")
        );

        return pbiPaymentRepository.findAll(spec, pageable);
    }

    public PbiPayment updatePbiPayment(UUID id, PbiPayment pbiPaymentDetails) {
        return pbiPaymentRepository.findById(id)
                .map(pbiPayment -> {
                    BeanUtils.copyProperties(pbiPaymentDetails, pbiPayment, "id");
                    return savePbiPayment(pbiPayment);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));
    }

    public PbiPayment partialUpdatePbiPayment(UUID id, Map<String, Object> updates) {
        PbiPayment pbiPayment = pbiPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));

        updates.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(PbiPayment.class, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pbiPayment, v);
            }
        });

        return savePbiPayment(pbiPayment);
    }

    public void deletePbiPayment(UUID id) {
        if (!pbiPaymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id " + id);
        }
        pbiPaymentRepository.deleteById(id);
    }
}

