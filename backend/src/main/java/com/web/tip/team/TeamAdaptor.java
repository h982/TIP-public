package com.web.tip.team;

public class TeamAdaptor {

    public static TeamDto entityToDto(Team team) {

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .projectId(team.getProjectId())
                .build();
    }
}

