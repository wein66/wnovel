package kr.co.musicart.wnovel.config;

import kr.co.musicart.wnovel.entity.User;
import kr.co.musicart.wnovel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 관리자 계정이 존재하는지 확인하고, 없으면 생성
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("1234")); // 초기 비밀번호
            admin.setNickname("관리자");
            admin.setRole(User.Role.ROLE_ADMIN);
            admin.setPoint(999999); // 관리자는 포인트 넉넉하게

            userRepository.save(admin);
            System.out.println(">>> 초기 관리자 계정이 생성되었습니다. (ID: admin / PW: 1234)");
        }
    }
}