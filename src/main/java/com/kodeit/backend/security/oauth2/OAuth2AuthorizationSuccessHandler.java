package com.kodeit.backend.security.oauth2;

import com.kodeit.backend.security.jwt.JWTTokenUtil;
import com.kodeit.backend.service.UserService;
import com.kodeit.backend.util.URLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class OAuth2AuthorizationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Value("${log.access-token}")
    private Boolean logAccessToken;

    @Autowired
    private URLBuilder urlBuilder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Convert the oauth into authentication object
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String avatar = oauthUser.getAttribute("picture");
        String name = oauthUser.getAttribute("name");

        String accessToken = jwtTokenUtil.generateAccessToken(email);
        String refreshToken = jwtTokenUtil.generateRefreshToken(email);

        // For development purpose. Disabled in production build
        if (logAccessToken)
            System.out.println(accessToken);

        // Checks if the user exists. If no, a user is created
        if (!userService.existsByEmail(email))
            userService.save(name, email, avatar);

        // Then the access and refresh tokens are sent over to the frontend
        response.sendRedirect(urlBuilder.frontendSigninRedirect(accessToken, refreshToken));
    }
}
