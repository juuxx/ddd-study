package com.study.ddd.config.security;

import static com.study.ddd.config.security.WebSecurityConfig.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
        ServletException {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            Cookie authCookie = new Cookie(AUTHCOOKIENAME, URLEncoder.encode(encryptId(user), "UTF-8"));
            authCookie.setPath("/");
            response.addCookie(authCookie);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        response.sendRedirect("/home");
    }

    private String encryptId(UserDetails user) {
        return user.getUsername();
    }
}
