package com.example.gal.domain.members.controller;

import com.example.gal.domain.members.dto.AddMemberRequest;
import com.example.gal.domain.members.dto.ChangePasswordRequest;
import com.example.gal.domain.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth") //auth 주소를 가로챔
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/join")
    public String join( AddMemberRequest request) { //join.html 을 사용하는곳에 모두.
        return "join";
    } //ui 렌더링

    @PostMapping("/join")
    public String join(
            @Valid AddMemberRequest request,
            BindingResult result //@Valid BindingResult 는 꼭 붙어있어야해.
            ) {
        if (result.hasErrors()) { //valid 에 이러가 없으면
            return "join";
        }
        try {
            memberService.addMember(request);
        }catch (DataIntegrityViolationException e) {
            result.reject("duplicated", "사용중인 Username입니다.");
            return "join";
        }catch(IllegalArgumentException ignored){
            result.reject("duplicated", "사용중인 학번입니다.");
            return "join";
        }catch (Exception e) {
            result.reject("error", e.getMessage());
            return "join";
        }
        return "redirect:/"; //index
    } //db에 추가

    @PostMapping("/change-pw")
    public String changePassword(
            @Valid ChangePasswordRequest request,
            BindingResult result
    ){
        if (result.hasErrors()) { //valid 에 이러가 없으면
            return "change-pw";
        }
        if(!Objects.equals(request.getPassword3(), request.getPassword2())){
            result.reject("pwNotSame","새 비번과 확인란의 값이 동일하지 않습니다.");
            return "change-pw";
        }
        try{
            memberService.changePw(request.getUsername(),request.getPassword1(),request.getPassword2());
        }catch(IllegalArgumentException ignored){
            result.reject("notfound", "아이디와 일치하는 계정을 찾을 수 없음.");
            return "change-pw";
        }catch(DataIntegrityViolationException ignored){
            result.reject("notAllowed", "비밀번호가 틀림.");
            return "change-pw";
        }
        return "login";
    }

}

//unique는 유효성 체크에 포함x, 디비 문제
