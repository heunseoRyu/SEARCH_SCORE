package com.example.gal.domain.members.controller;

import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MemberService memberService;

    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/members") //로그인 하고 볼 수 있는 페이지에는 null체크 할 필요 없. but, 아닌 경우에 요청되면 null일 수 있으므로
    public String member(Model model, Principal principal){
        Member member = memberService.getMember(principal.getName());
        model.addAttribute(member);
        return "member";
    }
}
//authentiacation : principal(객체), credential, authority
