package com.example.gal.domain.files.repo;

import com.example.gal.domain.files.domain.File;
import com.example.gal.domain.members.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {
    boolean existsByTestName(String testName);

    @EntityGraph(attributePaths = {"member"})
    List<File> findAllByMember(Member member);

}
