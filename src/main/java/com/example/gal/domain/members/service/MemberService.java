package com.example.gal.domain.members.service;

import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.dto.AddMemberRequest;
import com.example.gal.domain.members.dto.AddMemberResponse;
import com.example.gal.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AddMemberResponse addMember(AddMemberRequest request) throws IllegalArgumentException {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        if(memberRepository.existsByGradeAndClsAndNum(request.getGrade(),request.getCls(), request.getNum())) {
            throw new IllegalArgumentException();
        }
        return new AddMemberResponse(memberRepository.save(request.toEntity(encodedPassword)));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElse(null);
    }

    public void changePw(String username, String password1, String password2) {
        // 1.
        Member member = getMember(username);
        if(member == null) {
            throw new IllegalArgumentException();
        }
        // 2.
        if(!passwordEncoder.matches(password1, member.getPassword())) {
            throw new DataIntegrityViolationException("비번 틀림.");
        }
        member.setPassword(passwordEncoder.encode(password2));
        memberRepository.save(member);
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    public void initPassword(Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        String encodedPassword = passwordEncoder.encode("1234");
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public void allowMember(Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        member.setAllowed(true);
        memberRepository.save(member);
    }
}
