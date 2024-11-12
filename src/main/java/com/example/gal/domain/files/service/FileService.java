package com.example.gal.domain.files.service;

import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.files.dto.StudentScore;
import com.example.gal.domain.files.repo.FileRepository;
import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FileService {

    private final FileRepository fileRepository;
    private final MongoTemplate mongoTemplate;
    private final MemberRepository memberRepository;

    public void saveExcel(String collectionName,MultipartFile file,Integer rowCnt,Integer term,Integer year,String username) {
        // 1. 성적 저장
        mongoTemplate.createCollection(collectionName);
        saveScore(file,term,year,collectionName,rowCnt,username);
    }

    private void saveScore(MultipartFile file, Integer term,Integer year,String collectionName, Integer rowCnt,String username) {
        List<StudentScore> scores = extractScores(term,year,file, rowCnt,collectionName,username);
        for (StudentScore score : scores)
            mongoTemplate.save(score, collectionName);
    }

    private List<StudentScore> extractScores(Integer term,Integer year,MultipartFile file, Integer rowCnt,String collectionName,String username) {
        List<StudentScore> scores = new ArrayList<>();

        Member member = memberRepository.findByUsername(username).orElse(null);

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // Assume first row is header, start from second row
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String grade = formatter.formatCellValue(row.getCell(0));  // 첫 번째 셀 (학년)
                    String cls = formatter.formatCellValue(row.getCell(1));      // 두 번째 셀 (반)
                    String num = formatter.formatCellValue(row.getCell(2));      // 세 번째 셀 (번호)

                    // List<String> 형태로 나머지 정보를 저장
                    List<String> additionalData = new ArrayList<>();
                    for (int j = 3; j < rowCnt; j++) { // rowCnt + 1 (X)
                        additionalData.add(formatter.formatCellValue(row.getCell(j)));
                    }

                    // StudentScore 객체 생성
                    StudentScore studentScore = StudentScore.builder()
                            .key(i)
                            .grade(grade)
                            .cls(cls)
                            .num(num)
                            .values(additionalData)
                            .isCheck(false).build();

                    if(i == 0){
                        mongoTemplate.save(studentScore,collectionName);
                        saveFile(term,year,file,i,collectionName,member);
                    }else {
                        scores.add(studentScore);
                    }
                }
            }
        } catch (IOException e) {
            // Handle exception (e.g., log it or rethrow)
            e.printStackTrace();
        }

        return scores;
    }

    // 2. 시험이름, 생성일 저장
    public void saveFile(Integer term,Integer year,MultipartFile file,Integer id,String testName,Member member) {
        System.out.println(testName);
        fileRepository.save(File.builder()
                        .term(term)
                        .year(year)
                .fileName(file.getOriginalFilename())
                        .menuKey(id)
                .createTime(LocalDateTime.now())
                .testName(testName)
                .member(member).build());
    }


    public boolean checkIsValidFile(String testName) {
        return fileRepository.existsByTestName(testName);
    }

    public List<File> getMyFiles(Member member) {
        return fileRepository.findAllByMember(member);
    }

    public StudentScore getFileMenu(File file) {
        // 쿼리 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(file.getMenuKey()));

        // 동적으로 filename에 해당하는 컬렉션을 조회하고 쿼리 실행
        StudentScore menu = mongoTemplate.findOne(query, StudentScore.class, file.getTestName());
        return menu;
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    public List<StudentScore> getScores(File file) {
        // 쿼리 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("key").ne(file.getMenuKey()));

        // 동적으로 filename에 해당하는 컬렉션을 조회하고 쿼리 실행
        List<StudentScore> scores = mongoTemplate.find(query, StudentScore.class, file.getTestName());
        return scores;
    }

    public void deleteScores(String testName) {
        // testName 컬렉션의 모든 데이터를 삭제
        mongoTemplate.remove(new Query(), testName);
    }

    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    public StudentScore getMyResult(Integer grade, Integer cls, Integer num, String testName) {
        // 쿼리 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("grade").is(String.valueOf(grade)));
        query.addCriteria(Criteria.where("cls").is(String.valueOf(cls)));
        query.addCriteria(Criteria.where("num").is(String.valueOf(num)));

        // 동적으로 filename에 해당하는 컬렉션을 조회하고 쿼리 실행
        StudentScore result = mongoTemplate.findOne(query, StudentScore.class, testName);
        return result;
    }

    public void modifyScoresToChecked(File file, Integer key) {
        // 쿼리 조건 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));

        // 업데이트할 필드 설정
        Update update = new Update();
        update.set("isCheck", true);

        // 데이터 업데이트
        mongoTemplate.updateFirst(query, update, file.getTestName());
        System.out.println("완료");
    }

    public List<File> getMyFilesByYearAndTerm(Member member,Integer year, Integer term) {
        return fileRepository.findAllByMemberAndYearAndTerm(member,year,term);
    }

    public List<File> getFilesByYearAndTerm(Integer year, Integer term) {
        return fileRepository.findAllByYearAndTerm(year,term);
    }
}
