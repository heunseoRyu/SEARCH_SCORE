package com.example.gal.domain.files.controller;


import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.files.dto.StudentScore;
import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.files.usecase.FileUseCase;
import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            @RequestParam("file") MultipartFile file,
            @RequestParam("term") Integer term,
            @RequestParam("year") Integer year,
            Principal principal
    ) {
        fileUseCase.register(title,term,year,file,principal.getName());
        return "redirect:/admin/my";
    }

    @PostMapping("/{id}")
    public String modifyFile(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("term") Integer term,
            @RequestParam("year") Integer year,
            Principal principal
    ) {
        fileUseCase.modify(id,file,term,year,principal.getName());
        return "redirect:/admin/my";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("year") Integer year,
            @RequestParam("term") Integer term,
            Model model
    ){
        model.addAttribute("files",fileService.getFilesByYearAndTerm(year,term));
        return "/files";
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

        model.addAttribute("file",file);

        StudentScore score = fileUseCase.getMyResult(member,file);
        model.addAttribute("score",score);

        StudentScore menu = fileUseCase.getMenu(file);
        model.addAttribute("menu",menu);
        return "my_result";
    }

    @GetMapping("/{id}/scores/{key}")
    public String modifyIsCheck(
            @PathVariable("id") Long id,
            @PathVariable("key") Integer key
    ){
        fileUseCase.modifyIsCheck(id,key);
        return "redirect:/files/"+ id.toString() +"/result";
    }

    @GetMapping("/{id}/remove")
    public String remove(
            @PathVariable("id") Long id
    ){
        fileUseCase.remove(id);
        return "redirect:/admin/my";
    }

    @GetMapping("/upload")
    public String upload(){
        return "/admin/upload";
    }

    @GetMapping("/form")
    public String form(HttpServletResponse response){
        fileUseCase.getForm(response);
        return "redirect:/admin/my";
    }

}
