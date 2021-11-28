package com.web.tip.team;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamDto {

    private String id;
    private String name;
    private String projectId;

}
