package com.example.gal.domain.members.controller;

import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.dto.ChangePasswordRequest;
import com.example.gal.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MemberService memberService;
    private final FileService fileService;

    @GetMapping()
    public String index(Authentication authentication, Model model) {
        boolean isAdmin = false;
        boolean isTeacher = false;
        if (authentication != null) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            isTeacher = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("TEACHER"));
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        return "index";
    }

    @GetMapping("/members") //로그인 하고 볼 수 있는 페이지에는 null체크 할 필요 없. but, 아닌 경우에 요청되면 null일 수 있으므로
    public String member(Model model, Principal principal){
        Member member = memberService.getMember(principal.getName());
        model.addAttribute(member);
        return "member";
    }

    @GetMapping("/change-pw")
    public String changePassword(ChangePasswordRequest request){
        return "change-pw";
    }

    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("members",memberService.getMembers());
        return "users";
    }

    @GetMapping("/members/{id}/init-pw")
    public String initPassword(@PathVariable Long id, Model model){
        memberService.initPassword(id);
        return "redirect:/users";
    }

    @GetMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id, Model model){
        memberService.deleteMember(id);
        return "redirect:/users";
    }

    @GetMapping("/members/{id}/allow")
    public String allowMember(@PathVariable Long id, Model model){
        memberService.allowMember(id);
        return "redirect:/users";
    }

    @GetMapping("all")
    public String all(Model model){
        model.addAttribute("files",fileService.getFiles());
        return "all";
    }
}
//authentiacation : principal(객체), credential, authority
