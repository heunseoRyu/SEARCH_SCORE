package com.example.gal.domain.files.repo;

import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.members.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {
    boolean existsByTestName(String testName);

    @EntityGraph(attributePaths = {"member"})
    List<File> findAllByMemberOrderByIdDesc(Member member);

    @EntityGraph(attributePaths = {"member"})
    List<File> findAllByMemberAndYearAndTermOrderByIdDesc(Member member, Integer year, Integer term);

    @EntityGraph(attributePaths = {"member"})
    List<File> findAllByYearAndTermOrderByIdDesc(Integer year, Integer term);

    @Query("select f from File f order by f.id desc")
    List<File> findAllOrderByIdDesc();
}
