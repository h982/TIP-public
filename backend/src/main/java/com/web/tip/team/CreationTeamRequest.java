package com.web.tip.team;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreationTeamRequest {
    String projectId;
    String teamName;
    List<String> memberList;
}
