package com.example.gal.domain.members.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig {

    @Bean
    public WebSecurityCustomizer configure(){ //보안 해제 ex) static // 시큐리티 적용 안 할
        return web -> web.ignoring()
//                .requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**"),
                        new AntPathRequestMatcher("/img/**")
                        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/auth/login"),
                                new AntPathRequestMatcher("/auth/join"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/change-pw"),
                                new AntPathRequestMatcher("/auth/change-pw"),
                                new AntPathRequestMatcher("/auth/login-2")
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN","TEACHER") // "/admin" 이하 모든 요청은 ADMIN 권한 필요
                        .requestMatchers(HttpMethod.POST, "/files").hasAnyAuthority("ADMIN","TEACHER") // POST 요청 /files는 ADMIN 권한 필요
                        .requestMatchers(HttpMethod.GET,"/files/{id}/remove").hasAnyAuthority("ADMIN","TEACHER")
                        .requestMatchers(HttpMethod.GET,"files/search").hasAnyAuthority("ADMIN","TEACHER")
                        .requestMatchers(HttpMethod.POST, "/files/{id}").hasAnyAuthority("ADMIN","TEACHER") // POST 요청 /files/{id}는 ADMIN 권한 필요
                        .requestMatchers(HttpMethod.GET, "/files/{id}/scores/{key}").hasAnyAuthority("ADMIN","TEACHER") // PATCH 요청 /files/{id}/scores/{key}는 STUDENT 권한 필요
                        .requestMatchers(HttpMethod.GET, "/files/{id}","files/upload","/users","/members/{id}/init-pw","members/{id}/delete","/members/{id}/allow").hasAnyAuthority("ADMIN","TEACHER") // GET 요청 /files/{id}는 ADMIN 권한 필요
                        .requestMatchers("/all").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/"))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout")) //AntPathRequestMatcher: 특정 엔드포인트만 인증없이! 접근 가능하게.
                        //logoutFilter operate //post요청만 처리: logoutUrl / 모든 요청처리 : logoutRequestMatcher
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
