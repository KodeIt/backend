package com.kodeit.backend.security.filter;

import com.kodeit.backend.entity.User;
import com.kodeit.backend.security.authentication.EmailPasswordAuthenticationToken;
import com.kodeit.backend.security.jwt.JWTTokenUtil;
import com.kodeit.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenUtil jwtTokenUtil;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtTokenUtil.retrieveTokenFromRequest(request);
            jwtTokenUtil.authorizeToken(token);
            String email = jwtTokenUtil.retrieveEmailFromToken(token);
            User user = userService.get(email);
            Authentication authentication = new EmailPasswordAuthenticationToken(user.getUsername(), "",
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !(request.getServletPath().startsWith("/api/private")
                || request.getServletPath().startsWith("/api/admin"));
    }
}