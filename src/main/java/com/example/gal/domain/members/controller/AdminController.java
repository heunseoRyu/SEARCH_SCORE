package com.example.gal.domain.members.controller;

import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final FileService fileService;

    @GetMapping("/my")
    public String login(Model model, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        model.addAttribute("files",fileService.getMyFiles(member));
        return "admin/my";
    }

    @GetMapping("/my/search")
    public String search(
            @RequestParam("year") Integer year,
            @RequestParam("term") Integer term,
            Model model,
            Principal principal
    ){
        Member member = memberService.getMember(principal.getName());
        model.addAttribute("files",fileService.getMyFilesByYearAndTerm(member,year,term));
        return "/admin/my";
    }

}
