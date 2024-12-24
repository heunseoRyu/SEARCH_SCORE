package com.example.gal.domain.members.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    STUDENT("STUDENT"),
    ADMIN("ADMIN"),
    TEACHER("TEACHER");

    private final String authority;
}
