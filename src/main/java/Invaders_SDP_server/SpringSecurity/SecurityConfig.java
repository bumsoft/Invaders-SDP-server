package Invaders_SDP_server.SpringSecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 정책 설정
                )
                .csrf(csrf->csrf.disable())

                .authorizeHttpRequests((authorizeRequests)-> authorizeRequests
                        .anyRequest().permitAll())

                .formLogin((formLogin)->formLogin
                                .loginProcessingUrl("/login")
                                .successHandler(customAuthenticationSuccessHandler()) // 로그인 성공 시 핸들러
                                .failureHandler(customAuthenticationFailureHandler()) // 로그인 실패 시 핸들러
                )

                //로그아웃 처리가 필요할까 싶긴함(지금은 안쓰이고있음)
                .logout((logout)->logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")) //세션삭제
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    // 서비스와 인코더를 이용해인증과 권한부여 프로세스를 처리함.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //로그인 성공시 클라이언트로 전달하는 거(이렇게 전달해줘야 세션쿠키라 클라로 전달됨)
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication) throws IOException, ServletException
            {
                // 로그인 성공 시 리다이렉트를 비활성화하고, HTTP 응답 코드 200을 반환
                response.setStatus(HttpServletResponse.SC_OK);
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            // 실패 시 응답 코드 401 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }
}
