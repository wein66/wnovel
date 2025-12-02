package kr.co.musicart.wnovel.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.musicart.wnovel.dto.UserCreateDto;
import kr.co.musicart.wnovel.dto.UserUpdateDto;
import kr.co.musicart.wnovel.entity.User;
import kr.co.musicart.wnovel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 모든 사용자 목록을 조회합니다.
     * @return 사용자 리스트
     */
    public List<User> findUsers() {
        return userRepository.findAll();
    }

    /**
     * ID로 사용자를 조회합니다.
     * @param id 사용자 ID
     * @return 사용자 엔티티
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public void updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = findUserById(id);

        // DTO에 값이 있을 경우에만 필드를 업데이트합니다 (부분 업데이트 지원).
        if (userUpdateDto.getRole() != null) {
            user.setRole(userUpdateDto.getRole());
        }
        if (userUpdateDto.getPoint() != null) {
            user.setPoint(userUpdateDto.getPoint());
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User createUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setUsername(userCreateDto.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setNickname(userCreateDto.getNickname());
        user.setRole(userCreateDto.getRole());
        user.setPoint(userCreateDto.getPoint());

        return userRepository.save(user);
    }
}