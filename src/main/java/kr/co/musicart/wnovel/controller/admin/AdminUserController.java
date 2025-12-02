package kr.co.musicart.wnovel.controller.admin;

import kr.co.musicart.wnovel.dto.UserUpdateDto;
import kr.co.musicart.wnovel.dto.UserCreateDto;
import kr.co.musicart.wnovel.entity.User;
import kr.co.musicart.wnovel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user") // WebMvcConfig에 의해 /admin/user 로 매핑됨
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 회원 목록 페이지
     */
    @GetMapping("/list")
    public String userList(Model model) {
        List<User> users = userService.findUsers();
        model.addAttribute("users", users);
        return "admin/user/list";
    }

    /**
     * 회원 등록 페이지
     */
    @GetMapping("/write")
    public String writeUserForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        model.addAttribute("roles", User.Role.values());
        return "admin/user/write";
    }

    /**
     * 회원 등록 처리
     */
    @PostMapping("/write")
    public String writeUser(@ModelAttribute UserCreateDto userCreateDto) {
        userService.createUser(userCreateDto);
        return "redirect:/admin/user/list";
    }

    /**
     * 회원 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", User.Role.values()); // 모든 Role enum 값을 모델에 추가
        return "admin/user/edit";
    }

    /**
     * 회원 수정 처리
     */
    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, @ModelAttribute UserUpdateDto userUpdateDto) {
        userService.updateUser(id, userUpdateDto);
        return "redirect:/admin/user/list";
    }

    /**
     * 회원 삭제 처리
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        // 자기 자신은 삭제할 수 없도록 하는 로직 추가를 권장합니다.
        // 예: SecurityContextHolder에서 현재 로그인한 사용자 정보를 가져와 비교
        userService.deleteUser(id);
        return "redirect:/admin/user/list";
    }
}