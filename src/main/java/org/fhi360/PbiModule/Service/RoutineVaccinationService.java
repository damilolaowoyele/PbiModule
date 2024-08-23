package org.fhi360.PbiModule.Service;

import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Model.PbiEmployeeInactivity.PbiEmployeeInactivity;
import org.fhi360.PbiModule.Model.RoutineVaccination;
import org.fhi360.PbiModule.Repository.RoutineVaccinationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineVaccinationService {
    private final RoutineVaccinationRepository repository;

    public List<RoutineVaccination> getVaccinationsByTeam(String team) {
        return repository.findByTeamIgnoreCase(team);
    }

    public long countDistinctDatesByEmployeeAndDateRange(String username, LocalDate fromDate, LocalDate toDate, List<PbiEmployeeInactivity> inactivities) {
        List<LocalDate> inactiveDates = inactivities.stream()
                .flatMap(inactivity -> inactivity.getStartDate().datesUntil(inactivity.getEndDate().plusDays(1)))
                .collect(Collectors.toList());

        List<RoutineVaccination> vaccinations = getVaccinationsByTeam(username);
        return vaccinations.stream()
                .flatMap(v -> v.getVaccinationDates().values().stream())
                .flatMap(map -> map.values().stream())
                .filter(date -> !date.isBefore(fromDate) && !date.isAfter(toDate) && !inactiveDates.contains(date))
                .distinct()
                .count();
    }

    public int countVaccinationsByEmployeeAndDateRange(String username, LocalDate fromDate, LocalDate toDate, List<PbiEmployeeInactivity> inactivities) {
        List<LocalDate> inactiveDates = inactivities.stream()
                .flatMap(inactivity -> inactivity.getStartDate().datesUntil(inactivity.getEndDate().plusDays(1)))
                .collect(Collectors.toList());

        List<RoutineVaccination> vaccinations = getVaccinationsByTeam(username);
        return (int) vaccinations.stream()
                .flatMap(v -> v.getVaccinationDates().values().stream())
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> {
                    LocalDate date = entry.getValue();
                    return !date.isBefore(fromDate) && !date.isAfter(toDate) && !inactiveDates.contains(date);
                })
                .count();
    }

    public long countPersonsVaccinatedByEmployeeAndDateRange(String username, LocalDate fromDate, LocalDate toDate, List<PbiEmployeeInactivity> inactivities) {
        List<LocalDate> inactiveDates = inactivities.stream()
                .flatMap(inactivity -> inactivity.getStartDate().datesUntil(inactivity.getEndDate().plusDays(1)))
                .collect(Collectors.toList());

        List<RoutineVaccination> vaccinations = getVaccinationsByTeam(username);
        return vaccinations.stream()
                .filter(v -> v.getVaccinationDates().values().stream()
                        .flatMap(map -> map.values().stream())
                        .anyMatch(date -> !date.isBefore(fromDate) && !date.isAfter(toDate) && !inactiveDates.contains(date)))
                .map(RoutineVaccination::getId)
                .distinct()
                .count();
    }
}
