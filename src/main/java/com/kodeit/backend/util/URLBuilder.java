package com.kodeit.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class URLBuilder {

    @Value("${url.domain}")
    private String domain;

    @Value("${url.user-home.redirect}")
    private String homeRedirect;

    @Value("${url.forbidden.redirect}")
    private String forbiddenRedirect;

    @Value("${url.signin.redirect}")
    private String signinRedirect;

    public String frontendHomeUrl() {
        return domain + homeRedirect;
    }

    public String frontendForbiddenRedirect() {
        return domain + forbiddenRedirect;
    }

    public String frontendSigninRedirect(String accessToken, String refreshToken) {
        return domain + signinRedirect + "?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
    }

}
