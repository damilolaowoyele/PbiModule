package org.fhi360.PbiModule.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Data
@NoArgsConstructor
@Entity
@Table(name = "routine_vaccination")
public class RoutineVaccination {
    @Id
    private String id;

    @Column(name = "vaccination_no")
    private String vaccinationNo;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "team")
    private String team;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Transient
    private Map<String, Map<String, LocalDate>> vaccinationDates;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostLoad
    private void postLoad() {
        vaccinationDates = new HashMap<>();
        vaccinationDates.put("from_birth", extractDates(from_birth));
        vaccinationDates.put("from_6_months", extractDates(from_6_months));
        vaccinationDates.put("from_6_weeks", extractDates(from_6_weeks));
        vaccinationDates.put("from_9_months", extractDates(from_9_months));
        vaccinationDates.put("from_9_years", extractDates(from_9_years));
        vaccinationDates.put("from_10_weeks", extractDates(from_10_weeks));
        vaccinationDates.put("from_12_months", extractDates(from_12_months));
        vaccinationDates.put("from_14_weeks", extractDates(from_14_weeks));
        vaccinationDates.put("from_15_months", extractDates(from_15_months));
    }

    private Map<String, LocalDate> extractDates(String json) {
        Map<String, LocalDate> dates = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode valueNode = entry.getValue();

                // Only attempt to parse textual values that are not related to LatLng fields
                if (valueNode.isTextual() && !key.endsWith("LatLng") && !key.endsWith("Team")) {
                    String dateStr = valueNode.asText();

                    // Check if the textual content is a valid date
                    if (isValidDate(dateStr)) {
                        try {
                            LocalDate date = LocalDate.parse(dateStr);
                            if (date.isAfter(LocalDate.of(1900, 1, 1))) {
                                dates.put(key, date);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing date for key " + key + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading JSON: " + e.getMessage());
        }
        return dates;
    }

    private boolean isValidDate(String dateStr) {
        // Attempt to parse the date to validate it
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    // Lob fields
    @Type(PostgresJsonUserType.class)
    @Column(name = "from_birth", columnDefinition = "json")
    private String from_birth;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_6_months", columnDefinition = "json")
    private String from_6_months;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_6_weeks", columnDefinition = "json")
    private String from_6_weeks;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_9_months", columnDefinition = "json")
    private String from_9_months;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_9_years", columnDefinition = "json")
    private String from_9_years;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_10_weeks", columnDefinition = "json")
    private String from_10_weeks;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_12_months", columnDefinition = "json")
    private String from_12_months;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_14_weeks", columnDefinition = "json")
    private String from_14_weeks;

    @Type(PostgresJsonUserType.class)
    @Column(name = "from_15_months", columnDefinition = "json")
    private String from_15_months;

    @PrePersist
    private void prePersist() {
        from_birth = convertDatesToJson(vaccinationDates.get("from_birth"));
        from_6_months = convertDatesToJson(vaccinationDates.get("from_6_months"));
        from_6_weeks = convertDatesToJson(vaccinationDates.get("from_6_weeks"));
        from_9_months = convertDatesToJson(vaccinationDates.get("from_9_months"));
        from_9_years = convertDatesToJson(vaccinationDates.get("from_9_years"));
        from_10_weeks = convertDatesToJson(vaccinationDates.get("from_10_weeks"));
        from_12_months = convertDatesToJson(vaccinationDates.get("from_12_months"));
        from_14_weeks = convertDatesToJson(vaccinationDates.get("from_14_weeks"));
        from_15_months = convertDatesToJson(vaccinationDates.get("from_15_months"));
    }

    private String convertDatesToJson(Map<String, LocalDate> dates) {
        try {
            return objectMapper.writeValueAsString(dates);
        } catch (Exception e) {
            System.err.println("Error converting dates to JSON: " + e.getMessage());
            return "{}";
        }
    }

}


