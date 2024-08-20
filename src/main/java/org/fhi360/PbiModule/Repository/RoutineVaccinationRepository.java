package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.RoutineVaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineVaccinationRepository extends JpaRepository<RoutineVaccination, String> {
    List<RoutineVaccination> findByTeam(String team);

    @Query("SELECT rv FROM RoutineVaccination rv WHERE LOWER(rv.team) = LOWER(:team)")
    List<RoutineVaccination> findByTeamIgnoreCase(@Param("team") String team);

}



