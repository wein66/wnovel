package kr.co.musicart.wnovel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // [중요] 로그인 페이지, 정적 리소스, 그리고 에러 페이지(/error)는 누구나 접근 가능해야 함
                // /error: 컨트롤러나 템플릿 오류 시 발생하는 에러 페이지 접근 허용 (무한 루프 방지용 필수 설정)
                // /favicon.ico: 브라우저가 자동으로 요청하는 아이콘 파일
                .requestMatchers("/admin/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/api/user/register").permitAll()
                .requestMatchers("/", "/novel/**", "/api/novel/**").permitAll()
                
                // [중요] 나머지 /admin/** 경로는 관리자만 접근 가능
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}