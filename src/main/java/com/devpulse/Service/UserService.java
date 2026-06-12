package com.devpulse.Service;

import com.devpulse.Entity.User;
import com.devpulse.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveOrUpdateUser(
            String githubId,
            String username,
            String avatarUrl,
            String email,
    String accessToken) {

        User user = userRepository
                .findByGithubId(githubId)
                .orElse(new User());

        user.setGithubId(githubId);
        user.setUsername(username);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setGithubAccessToken(accessToken);
        return userRepository.save(user);
    }
}