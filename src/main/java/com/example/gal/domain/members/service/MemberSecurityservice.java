package com.example.gal.domain.members.service;

import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityservice implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() ->new UsernameNotFoundException("없는 사용자입니다.")) ;

        if(!member.isAllowed())
            throw new IllegalArgumentException("승인 대기중인 계정입니다.");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().getAuthority())); //Member 객체 안에 authorities 필드 저장 후 가져오기 (db에 권한 필드에 가져온다)

        return new User(member.getUsername(), member.getPassword(), authorities); //UserDetails 의 User를 반환
    } //security에 저장할건지는 spring security. username의 유저가 있는지만 찾아주면 됌.
    //UserDetails는 인터페이스이므로 더 복잡한 권한을 구현해 반환가능.
}
