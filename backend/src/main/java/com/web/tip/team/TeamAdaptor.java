package com.web.tip.team;

public class TeamAdaptor {

    private TeamAdaptor() {
        throw new IllegalStateException("Utility class");
    }

    public static TeamDto entityToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .projectId(team.getProjectId())
                .build();
    }
}

