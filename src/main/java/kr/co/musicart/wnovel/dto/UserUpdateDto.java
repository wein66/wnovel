package kr.co.musicart.wnovel.dto;

import kr.co.musicart.wnovel.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    
    private User.Role role;
    
    private Integer point;

}