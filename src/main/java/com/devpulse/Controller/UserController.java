package com.devpulse.Controller;

import com.devpulse.Dto.UserProfileDto;
import com.devpulse.Entity.User;
import com.devpulse.Repository.UserRepository;
import com.devpulse.Security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/user/me")
    public UserProfileDto me() {
        System.out.println("USER API HIT");
        Long id = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(id).orElseThrow();

        return new UserProfileDto(
                user.getUsername(),
                user.getAvatarUrl()
        );
    }

}