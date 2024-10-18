package org.fhi360.PbiModule.Model.PbiEmployee;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fhi360.PbiModule.Model.PbiEmployeeInactivity.PbiEmployeeInactivity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@ValidAccountName
@Data
@NoArgsConstructor
@Entity
@Table(name = "pbi_employee")
public class PbiEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must be at most 50 characters")
    @Column(name = "state", nullable = false, length = 50)
    private String state;


    @Size(max = 50, message = "Cluster must be at most 50 characters")
    @Column(name = "cluster", nullable = true, length = 50)
    private String cluster;

    @NotBlank(message = "LGA is required")
    @Size(max = 50, message = "LGA must be at most 50 characters")
    @Column(name = "lga", nullable = false, length = 50)
    private String lga;

    @NotNull(message = "Team is required")
    @Column(name = "team", nullable = false)
    private Integer team;

    @NotBlank(message = "TeamType is required")
    @Pattern(regexp = "^(Mobile|Facility)$", message = "Team type must be either 'Mobile' or 'Facility'")
    @Column(name = "team_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Mobile'")
    private String teamType;

    @NotBlank(message = "Facility is required")
    @Size(max = 100, message = "Facility must be at most 100 characters")
    @Column(name = "facility_attached", nullable = false, length = 100)
    private String facility;

    @NotBlank(message = "Team username is required")
    @Size(max = 50, message = "username must be at most 50 characters")
    @Column(name = "team_username", nullable = false, length = 50)
    private String teamUsername;

    @Column(name = "password", nullable = true, length = 50)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Middle name must be at most 50 characters")
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(Male|Female)$", message = "Sex must be either 'Male' or 'Female'")
    @Column(name = "sex", nullable = false)
    private String sex;

    @NotBlank(message = "Designation is required")
    @Pattern(regexp = "^(Manual Recorder|E-recorder|Mobilizer|Vaccinator|Facility E-recorder)$", message = "Invalid designation")
    @Column(name = "designation", nullable = false, length = 50)
    private String designation;

    @Column(name = "new_intake", nullable = false)
    private boolean newIntake;

    @Column(name = "previous_experience", nullable = false)
    private boolean previousExperience;

    @Size(max = 100, message = "Qualification must be at most 100 characters")
    @Column(name = "qualification", nullable = false, length = 100)
    private String qualification;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
//    @Pattern(regexp = "((^\\+)(234){1}[0-9]{10})|((^234)[0-9]{10})|((^0)(7|8|9){1}(0|1){1}[0-9]{8})\n", message = "Invalid Nigerian mobile phone number format")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

//    @NotBlank(message = "Internet phone number is required")
//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid internet phone number format")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$|^$", message = "Invalid internet phone number format")
//    @Pattern(regexp = "((^\\+)(234){1}[0-9]{10})|((^234)[0-9]{10})|((^0)(7|8|9){1}(0|1){1}[0-9]{8})\n", message = "Invalid Nigerian mobile phone number format")
    @Column(name = "internet_phone_number", nullable = true)
    private String internetPhoneNumber;

    @Column(name = "government_employee", nullable = false)
    private boolean governmentEmployee;

    @Email(message = "Invalid email format")
    @Column(name = "email_address")
    private String emailAddress;

    @Size(max = 255, message = "Home address must be at most 255 characters")
    @Column(name = "home_address", length = 255, nullable = false)
    private String homeAddress;

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must be at most 100 characters")
    @Column(name = "account_name", length = 100)
    private String accountName;

    @NotBlank(message = "Bank is required")
    @Size(max = 50, message = "Bank name must be at most 50 characters")
    @Column(name = "bank", length = 50)
    private String bank;

    @Pattern(regexp = "^[0-9]{10}$", message = "Account number must be 10 digits")
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Size(max = 50, message = "Means of identification must be at most 50 characters")
    @Column(name = "means_of_identification", length = 50)
    private String meansOfIdentification;

    @Size(max = 50, message = "ID number must be at most 50 characters")
    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Size(max = 50, message = "Sort code must be at most 50 characters")
    @Column(name = "sort_code", length = 50)
    private String sortCode;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be either 'Active' or 'Inactive'")
    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PbiEmployeeInactivity> periodsOfInactivity;

    @Column(name = "suspected_duplicate", nullable = false)
    private boolean suspectedDuplicate = false;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public void setDesignation(String designation) {
        this.designation = designation;
        if (designation.equals("Facility E-recorder")) {
            this.teamType = "Facility";
        } else {
            this.teamType = "Mobile";
        }
    }

    public void setPeriodsOfInactivity(List<PbiEmployeeInactivity> periodsOfInactivity) {
        if (this.periodsOfInactivity == null) {
            this.periodsOfInactivity = new ArrayList<>();
        }
        this.periodsOfInactivity.clear();
        if (periodsOfInactivity != null) {
            this.periodsOfInactivity.addAll(periodsOfInactivity);
        }
    }

}
