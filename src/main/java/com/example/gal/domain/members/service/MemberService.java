package com.example.gal.domain.members.service;

import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.domain.MemberRole;
import com.example.gal.domain.members.dto.AddMemberRequest;
import com.example.gal.domain.members.dto.AddMemberResponse;
import com.example.gal.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.gal.domain.members.domain.MemberRole.STUDENT;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AddMemberResponse addMember(AddMemberRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        return new AddMemberResponse(memberRepository.save(request.toEntity(encodedPassword)));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElse(null);
    }
}
