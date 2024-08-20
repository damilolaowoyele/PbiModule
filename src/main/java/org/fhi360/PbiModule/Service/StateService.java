package org.fhi360.PbiModule.Service;

import org.fhi360.PbiModule.Model.StatesLgasFacilities.State;
import org.fhi360.PbiModule.Repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }
}
