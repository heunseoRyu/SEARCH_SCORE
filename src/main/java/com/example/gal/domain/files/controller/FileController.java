package com.example.gal.domain.files.controller;


import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.files.dto.StudentScore;
import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.files.usecase.FileUseCase;
import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUseCase fileUseCase;
    private final FileService fileService;
    private final MemberService memberService;

    @PostMapping("")
    public String uploadFile(
            @RequestParam("title") String title,
            @RequestParam("rowCnt") Integer rowCnt,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        fileUseCase.register(title,rowCnt,file,principal.getName());
        return "redirect:/admin/my";
    }

    @PostMapping("/{id}")
    public String uploadFile(
            @PathVariable("id") Long id,
            @RequestParam("rowCnt") Integer rowCnt,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        fileUseCase.modify(id,rowCnt,file,principal.getName());
        return "redirect:/admin/my";
    }

    @GetMapping("/{id}")
    public String getFileInfo(
            @PathVariable("id") Long id,
            Model model
    ){
        File file = fileService.getFileById(id);
        model.addAttribute("file",file);
        StudentScore menu = fileUseCase.getMenu(file);
        model.addAttribute("menu",menu);
        List<StudentScore> scores = fileUseCase.getScores(file);
        model.addAttribute("scores",scores);
        return "/admin/file_info";
    }

    @GetMapping("")
    public String getFiles(Model model){
        List<File> files = fileService.getFiles();
        model.addAttribute("files",files);
        return "/files";
    }

    @GetMapping("/{id}/result")
    public String getMyResult(@PathVariable("id") Long id,Model model,Principal principal){
        Member member = memberService.getMember(principal.getName());
        File file = fileService.getFileById(id);

        StudentScore score = fileUseCase.getMyResult(member,file);
        model.addAttribute("score",score);

        StudentScore menu = fileUseCase.getMenu(file);
        model.addAttribute("menu",menu);
        return "my_result";
    }

}
