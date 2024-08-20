package org.fhi360.PbiModule.Controller;

import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Model.PbiPayment;
import org.fhi360.PbiModule.Service.PaymentCalculationService;
import org.fhi360.PbiModule.Service.PbiPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/pbiPayments")
@RequiredArgsConstructor
public class PbiPaymentController {

    private final PbiPaymentService pbiPaymentService;
    private final PaymentCalculationService paymentCalculationService;
    private static final Logger logger = LoggerFactory.getLogger(PbiPaymentController.class);

    @GetMapping("/generate")
    public ResponseEntity<Page<PbiPayment>> generatePayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String lga,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String teamType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PbiPayment> paymentsPage = paymentCalculationService.calculatePayments(fromDate, toDate, searchTerm, state, lga, facility, teamType, pageable);
        return ResponseEntity.ok(paymentsPage);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String lga,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String teamType) {
        try {
            logger.info("Starting export for payments from {} to {}", fromDate, toDate);

            // Use Pageable.unpaged() for exporting all data without pagination
            List<PbiPayment> payments = paymentCalculationService.calculatePayments(
                    fromDate, toDate, searchTerm, state, lga, facility, teamType, Pageable.unpaged()).getContent();

            String fileName = "pbi_payments_" + fromDate.format(DateTimeFormatter.ISO_DATE) + "_to_" +
                    toDate.format(DateTimeFormatter.ISO_DATE) + ".csv";
            Path tempFile = Files.createTempFile("pbi_payments_", ".csv");

            paymentCalculationService.exportPaymentsToCsv(payments, tempFile.toString());

            // Create an InputStreamResource to stream the file content
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(tempFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            logger.info("Export completed successfully for payments from {} to {}", fromDate, toDate);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error exporting payments to CSV", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> savePayments(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            logger.info("Starting save for payments from {} to {}", fromDate, toDate);

            List<PbiPayment> payments = paymentCalculationService.calculatePayments(
                    fromDate, toDate, null, null, null, null, null, Pageable.unpaged()).getContent();

            List<PbiPayment> savedPayments = pbiPaymentService.saveAllPbiPayments(payments);

            logger.info("Save completed successfully for payments from {} to {}", fromDate, toDate);
            return ResponseEntity.ok(savedPayments);
        } catch (Exception e) {
            logger.error("Error saving payments", e);
            return ResponseEntity.internalServerError().body("Error saving payments: " + e.getMessage());
        }
    }

    @PostMapping("/saveFiltered")
    public ResponseEntity<?> savePayments(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                         @RequestParam(required = false) String searchTerm,
                                                         @RequestParam(required = false) String state,
                                                         @RequestParam(required = false) String lga,
                                                         @RequestParam(required = false) String facility,
                                                         @RequestParam(required = false) String teamType) {
        try {
            logger.info("Starting save for payments from {} to {}", fromDate, toDate);

            List<PbiPayment> payments = paymentCalculationService.calculatePayments(
                    fromDate, toDate, searchTerm, state, lga, facility, teamType, Pageable.unpaged()).getContent();

            List<PbiPayment> savedPayments = pbiPaymentService.saveAllPbiPayments(payments);

            logger.info("Save completed successfully for payments from {} to {}", fromDate, toDate);
            return ResponseEntity.ok(savedPayments);
        } catch (Exception e) {
            logger.error("Error saving payments", e);
            return ResponseEntity.internalServerError().body("Error saving payments: " + e.getMessage());
        }

    }

}
