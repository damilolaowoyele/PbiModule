package org.fhi360.PbiModule.Model;

import lombok.Getter;

@Getter
public enum Team {
    TEAM_ONE(1),
    TEAM_TWO(2),
    TEAM_THREE(3),
    TEAM_FOUR(4);

    private final int value;

    Team(int value) {
        this.value = value;
    }

    public static Team fromValue(int value) {
        for (Team team : Team.values()) {
            if (team.getValue() == value) {
                return team;
            }
        }
        throw new IllegalArgumentException("Invalid team value: " + value);
    }
}
