package org.fhi360.PbiModule.Model.StatesLgasFacilities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Data
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "states_lgas_facilities")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "state")
    private String state;

    @Type(LgasFacilitiesJsonUserType.class)
    @Column(name = "lgas_facilities", columnDefinition = "json")
    private LgasFacilities lgasFacilities;
}

