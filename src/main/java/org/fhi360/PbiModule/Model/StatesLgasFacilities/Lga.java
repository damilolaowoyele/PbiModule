package org.fhi360.PbiModule.Model.StatesLgasFacilities;

import lombok.Data;

import java.util.List;

@Data
public class Lga {
    private String lga;
    private List<String> facilities;
}
