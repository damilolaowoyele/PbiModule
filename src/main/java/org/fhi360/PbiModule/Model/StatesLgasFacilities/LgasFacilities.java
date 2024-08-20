package org.fhi360.PbiModule.Model.StatesLgasFacilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LgasFacilities {

    @JsonProperty("lgas")
    private List<Lga> lgas;

}
