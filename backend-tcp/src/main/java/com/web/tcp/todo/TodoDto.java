package com.web.tcp.todo;

import lombok.*;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDto {

    @Id
    private String id;

    private String title;
    private String status;

    private String projectId;

    private String memberId;
    private String memberName;

    private String teamId;
    private String teamName;

    private boolean isBookmark;

    private LocalDateTime modifyDate;
    private LocalDateTime regDate;
}