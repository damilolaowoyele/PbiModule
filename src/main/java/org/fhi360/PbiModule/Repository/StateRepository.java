package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.StatesLgasFacilities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository<State, UUID> {
}
