package com.ll.clearpath.domain.member.member.entity;

import com.ll.clearpath.domain.member.myPage.dto.UpdateMemberInterestsDto;
import com.ll.clearpath.domain.member.myPage.dto.UpdateMemberNicknameDto;
import com.ll.clearpath.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String username;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String providerId;

    private String interests;

    public void updateMemberNickname(UpdateMemberNicknameDto updateMemberNicknameDto){
        this.nickname = updateMemberNicknameDto.getNickname();
    }

    public void updateMemberPassword(String newPassword){
        this.password = newPassword;
    }

    public void updateMemberInterests(UpdateMemberInterestsDto updateMemberInterestsDto){
        this.interests = updateMemberInterestsDto.getInterests();
    }

}