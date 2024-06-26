package com.ll.clearpath.domain.member.myPage.service;

import com.ll.clearpath.domain.member.member.entity.Member;
import com.ll.clearpath.domain.member.member.repository.MemberRepository;
import com.ll.clearpath.domain.member.myPage.dto.MemberInfoDto;
import com.ll.clearpath.domain.member.myPage.dto.UpdateMemberInterestsDto;
import com.ll.clearpath.domain.member.myPage.dto.UpdateMemberNicknameDto;
import com.ll.clearpath.domain.member.myPage.dto.UpdateMemberPasswordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberInfoDto getMemberInfo(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        Member member = optionalMember.get();
        return new MemberInfoDto(member.getEmail(), member.getUsername(), member.getNickname(), member.getInterests());
    }

    @Transactional
    public void updateNickname(UpdateMemberNicknameDto updateMemberNicknameDto, Long id) {
        Member member = memberRepository.findById(id).get();
        member.updateMemberNickname(updateMemberNicknameDto);
    }

    public boolean checkCurrentPassword(UpdateMemberPasswordDto updateMemberPasswordDto, Long id) {
        Member member = memberRepository.findById(id).get();
        return passwordEncoder.matches(updateMemberPasswordDto.getPassword(), member.getPassword());
    }

    public boolean checkNewPassword(UpdateMemberPasswordDto updateMemberPasswordDto) {
        return updateMemberPasswordDto.getNewPassword().equals(updateMemberPasswordDto.getNewPasswordConfirm());
    }

    @Transactional
    public void updatePassword(UpdateMemberPasswordDto updateMemberPasswordDto, Long id) {
        Member member = memberRepository.findById(id).get();
        member.updateMemberPassword(passwordEncoder.encode(updateMemberPasswordDto.getNewPassword()));
    }

    public boolean checkNickname(UpdateMemberNicknameDto updateMemberNicknameDto, Long id) {
        Optional<Member> optionalMember = memberRepository.findByNickname(updateMemberNicknameDto.getNickname());
        return optionalMember.isPresent() && !optionalMember.get().getId().equals(id);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).get();
        memberRepository.delete(member);
    }

    public boolean IsSocialLogin(Long id) {
        String social = memberRepository.findById(id).get().getProviderId();
        return social != null;
    }

    @Transactional
    public void updateInterests(UpdateMemberInterestsDto updateMemberInterestsDto, Long id) {
        Member member = memberRepository.findById(id).get();
        member.updateMemberInterests(updateMemberInterestsDto);
    }
}