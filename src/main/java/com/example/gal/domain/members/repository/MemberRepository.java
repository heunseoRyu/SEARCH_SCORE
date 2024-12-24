package com.example.gal.domain.members.repository;

import com.example.gal.domain.members.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //optional 로 반환되는건 unique 라는 뜻.
    Optional<Member> findByUsername(String username);

    boolean existsByGradeAndClsAndNum(Integer grade, Integer cls, Integer num);

    @Query("select m from Member m where m.authority != 'ADMIN'")
    List<Member> findAllByAuthority();
}
