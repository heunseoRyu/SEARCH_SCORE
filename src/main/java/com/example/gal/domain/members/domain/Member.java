package com.example.gal.domain.members.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "tb_member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) //unique니 중복체크 해줘야 한다. -> controller / DataIntegrityViolationException
    private String username;

    @Column(nullable = false)
    private String password;

    private Integer grade;

    private Integer cls;

    private Integer num;

    private boolean isAllowed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole authority;

}
