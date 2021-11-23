package com.web.tip.team;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Team {
    @Id
    private String id;
    private String name;
    private String projectId;
    private boolean isUse;

    public void changeName(String name){
        this.name = name;
    }

    public void deleteTeam(){
        this.isUse = false;
    }

}
