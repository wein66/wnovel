package kr.co.musicart.wnovel.dto;

import kr.co.musicart.wnovel.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String username;
    private String password;
    private String nickname;
    private User.Role role;
    private int point;
}