package com.example.gal.domain.members.dto;

import com.example.gal.domain.members.domain.Member;
import com.example.gal.domain.members.domain.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.example.gal.domain.members.domain.MemberRole.STUDENT;

@Getter
@Setter
public class AddMemberRequest {
    @NotEmpty(message = "username은 필수입니다.")
    private String username; //요청시 비어있으면 안됌

    @NotEmpty(message = "비밀번호는 필수입니다.")
    @Size(min=3, max=30, message = "비밀번호는 3자~20자 입니다.")
    private String password;

    @NotEmpty(message = "비밀번호를 한번 더 입력하세요")
    @Size(min=3, max=30, message = "비밀번호는 3자~20자 입니다.")
    private String password2;

    @NotEmpty(message = "이름을 입력하세요")
    @Size(min=3, max=4, message = "이름은 3자~4자 입니다.")
    private String name;

    private Integer grade;

    private Integer cls;

    private Integer num;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .username(this.username)
                .password(encodedPassword)
                .grade(this.grade)
                .cls(this.cls)
                .num(this.num)
                .name(this.name)
                .authority(STUDENT).build();
    }
}
