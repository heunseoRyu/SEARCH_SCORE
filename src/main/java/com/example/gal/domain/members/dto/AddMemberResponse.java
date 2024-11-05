package com.example.gal.domain.members.dto;

import com.example.gal.domain.members.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddMemberResponse {
    private Long id;
    private String username;

    public  AddMemberResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
