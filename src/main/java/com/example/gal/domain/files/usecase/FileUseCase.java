package com.example.gal.domain.files.usecase;

import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.files.dto.StudentScore;
import com.example.gal.domain.files.repo.FileRepository;
import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.members.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FileUseCase {

    private final FileService fileService;
    private final FileRepository fileRepository;

    public void register(String title, Integer rowCnt, MultipartFile file,String username) {
        // 1. 최초 저장하는 시험인지 확인
        if(fileService.checkIsValidFile(title)) {
            System.out.println("이미 존재하는 시험입니다.");
        }
        // 2. file 저장
       else {
           fileService.saveExcel(title,file,rowCnt,username);
        }
    }

    public void modify(Long id, Integer rowCnt, MultipartFile file, String username) {
        File fileEntity = fileService.getFileById(id);
        fileService.deleteScores(fileEntity.getTestName());
        fileService.saveExcel(fileEntity.getTestName(),file,rowCnt,username);
        fileRepository.delete(fileEntity);
    }

    public StudentScore getMenu(File file) {
//        // 0. file 찾기
//        File file = fileService.getFileById(id);
//        System.out.println(file.getFileName());
        // 1. menu먼저
        return fileService.getFileMenu(file);
    }

    public List<StudentScore> getScores(File file) {
//        // 0. file 찾기
//        File file = fileService.getFileById(id);
        // 1. get scores
        return fileService.getScores(file);
    }


    public StudentScore getMyResult(Member member,File file) {
        return fileService.getMyResult(member.getGrade(),member.getCls(),member.getNum(),file.getTestName());
    }
}
