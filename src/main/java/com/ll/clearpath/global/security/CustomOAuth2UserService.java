package com.ll.clearpath.global.security;

import com.ll.clearpath.domain.member.member.entity.Member;
import com.ll.clearpath.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauthId = oAuth2User.getName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // OAuth2 프로바이더 타입을 식별합니다.
        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        String email = null;
        String name = null;
        String nickname = null;
        String providerId = null;

        switch (providerTypeCode) {
            case "KAKAO":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                name = (String) profile.get("nickname");
                nickname = providerTypeCode + "__%s".formatted(name);
                email = name + "@" + providerTypeCode + (int) (Math.random() * 100000);
                providerId = providerTypeCode + "__%s".formatted(oauthId);
                break;
            case "GOOGLE":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                nickname = providerTypeCode + "__%s".formatted(name);
                providerId = providerTypeCode + "__%s".formatted(oauthId);
                break;
        }

        // whenSocialLogin 메소드를 수정된 매개변수 목록에 맞게 호출합니다.
        Member member = memberService.whenSocialLogin(providerTypeCode ,providerId, nickname, name, email);

        // CustomUserDetails 객체를 생성하여 반환합니다.
        return new CustomUserDetails(member);
    }

}