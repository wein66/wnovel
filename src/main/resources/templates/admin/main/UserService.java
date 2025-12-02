package kr.co.musicart.wnovel.service;

import kr.co.musicart.wnovel.entity.User;
import kr.co.musicart.wnovel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 모든 회원 목록을 최신 가입순으로 조회합니다.
     */
    public List<User> getUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}