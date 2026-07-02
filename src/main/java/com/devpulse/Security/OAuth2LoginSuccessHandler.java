package com.devpulse.Security;


import com.devpulse.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.devpulse.Service.GitHubService;

import java.io.IOException;
//check
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final GitHubService gitHubService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Value("${app.frontend-url}")
    private String frontendUrl;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {
        System.out.println("SUCCESS HANDLER EXECUTED");
        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;
        Integer githubIdInt = oauthUser.getAttribute("id");
        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        token.getAuthorizedClientRegistrationId(),
                        token.getName());

        String accessToken =
                client.getAccessToken().getTokenValue();

        String githubId = String.valueOf(githubIdInt);

        String username =
                oauthUser.getAttribute("login");

        String avatarUrl =
                oauthUser.getAttribute("avatar_url");

        String email =
                oauthUser.getAttribute("email");

        var user = userService.saveOrUpdateUser(
                githubId,
                username,
                avatarUrl,
                email,
                accessToken);
        gitHubService.saveReposForUser(user.getId());
        gitHubService.syncAllCommitsForUser(user.getId());
        gitHubService.syncAllPullRequestsForUser(user.getId());
        String jwt =
                jwtService.generateToken(user.getId());

        response.sendRedirect(
                frontendUrl + "/login-success?token=" + token
        );
    }
}
