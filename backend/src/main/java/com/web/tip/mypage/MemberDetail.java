package com.web.tip.mypage;

import com.web.tip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetail {
    @Id
    private String id;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String profileImg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setProfileImgLink(String profileImg){
        this.profileImg = profileImg;
    }

}
