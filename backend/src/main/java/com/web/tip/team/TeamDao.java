package com.web.tip.team;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamDao extends JpaRepository<Team, String> {
    List<Team> findTeamByProjectIdAndIsUse(String projectId, boolean isUse);

    List<Team> findTeamByIdIn(List<String> ids);

    Optional<Team> findTeamByProjectIdAndNameAndIsUse(String projectId, String name, boolean isUse);

    Optional<Team> findTeamById(String id);
}
