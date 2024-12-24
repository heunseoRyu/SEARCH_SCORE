package com.example.gal.domain.members.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "사용자 이름을 입력하세요.")
    private String username;

    @NotBlank(message = "기존 비밀번호를 입력하세요.")
    private String password1;

    @NotBlank(message = "새 비밀번호을 입력하세요.")
    @Size(min=3, max=30, message = "비밀번호는 3자~20자 입니다.")
    private String password2;

    @NotBlank(message = "비밀번호를 한번 더 입력하세요.")
    @Size(min=3, max=30, message = "비밀번호는 3자~20자 입니다.")
    private String password3;

}
