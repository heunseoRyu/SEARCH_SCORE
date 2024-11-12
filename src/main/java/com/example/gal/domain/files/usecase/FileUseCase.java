package com.example.gal.domain.files.usecase;

import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.files.dto.StudentScore;
import com.example.gal.domain.files.repo.FileRepository;
import com.example.gal.domain.files.service.FileService;
import com.example.gal.domain.members.domain.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FileUseCase {

    private final FileService fileService;
    private final FileRepository fileRepository;

    public void register(String title, Integer rowCnt, Integer term,Integer year,MultipartFile file,String username) {
        // 1. 최초 저장하는 시험인지 확인
        if(fileService.checkIsValidFile(title)) {
            System.out.println("이미 존재하는 시험입니다.");
        }
        // 2. file 저장
       else {
           fileService.saveExcel(title,file,rowCnt,term,year,username);
        }
    }

    public void modify(Long id, Integer rowCnt, MultipartFile file,Integer term,Integer year, String username) {
        File fileEntity = fileService.getFileById(id);
        fileService.deleteScores(fileEntity.getTestName());
        fileService.saveExcel(fileEntity.getTestName(),file,rowCnt,term,year,username);
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

    public void modifyIsCheck(Long id, Integer key) {
        File file = fileService.getFileById(id);
        fileService.modifyScoresToChecked(file,key);
    }

    public void remove(Long id) {
        File file = fileService.getFileById(id);
        fileService.deleteScores(file.getTestName());
        fileRepository.delete(file);
    }

    public void getForm(HttpServletResponse response) {
        // 엑셀 데이터 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("고객 명단");
        int rowNo = 0;

        Row headerRow = sheet.createRow(rowNo++);
        headerRow.createCell(0).setCellValue("학년");
        headerRow.createCell(1).setCellValue("반");
        headerRow.createCell(2).setCellValue("번호");
        headerRow.createCell(3).setCellValue("내용1");
        headerRow.createCell(4).setCellValue("내용2");
        headerRow.createCell(5).setCellValue("내용3");
        headerRow.createCell(6).setCellValue("...");

        Row row = sheet.createRow(rowNo++);
        row.createCell(0).setCellValue(2);
        row.createCell(1).setCellValue(2);
        row.createCell(2).setCellValue(2);
        row.createCell(3).setCellValue(50);
        row.createCell(4).setCellValue(60);
        row.createCell(5).setCellValue(70);
        row.createCell(6).setCellValue("...");

        // 엑셀 응답 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode("제출양식.xlsx", StandardCharsets.UTF_8) + "\"");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
