package com.kodeit.backend.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kodeit.backend.exception.jwt.JWTTokenBlacklistedException;
import com.kodeit.backend.security.authentication.EmailPasswordAuthenticationToken;
import com.kodeit.backend.service.UserService;
import com.kodeit.backend.util.StaticContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.stream;

@Component
public class JWTTokenUtil {

    @Autowired
    private UserService userService;
    @Value("${jwt.validity.access-token}")
    private Long ACCESS_TOKEN_VALIDITY_DURATION;
    @Value("${jwt.validity.refresh-token}")
    private Long REFRESH_TOKEN_VALIDITY_DURATION;
    @Value("${jwt.tag.issuer}")
    private String TOKEN_ISSUER;
    @Value("${jwt.tag.issued-date}")
    private String ISSUED_DATE_TAG;
    @Value("${jwt.tag.roles}")
    private String ROLES_TAG;
    @Value("${jwt.secret}")
    private String SECRET;

    public String generateAccessToken(String email){
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_DURATION))
                .withIssuer(TOKEN_ISSUER)
                .withClaim(ISSUED_DATE_TAG, new Date())
                .withClaim(ROLES_TAG, List.of("USER"))
                .sign(algorithm);
    }

    public String generateRefreshToken(String email){
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_DURATION))
                .withIssuer(TOKEN_ISSUER)
                .withClaim(ISSUED_DATE_TAG, new Date())
                .sign(algorithm);
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) throws JWTTokenBlacklistedException, AuthenticationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
        String email = retrieveEmailFromToken(refreshToken);
        if (StaticContextHolder.getInstance().jwtTokenExists(email, refreshToken)){
            throw new JWTTokenBlacklistedException();
        }
        userService.loadUserByUsername(email);
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_DURATION))
                .withIssuer(TOKEN_ISSUER)
                .withClaim(ROLES_TAG, List.of("USER"))
                .withClaim(ISSUED_DATE_TAG, new Date())
                .sign(algorithm);
    }

    public String retrieveTokenFromRequest(HttpServletRequest request){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public DecodedJWT getDecodedToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
        return JWT.require(algorithm).build().verify(token);
    }

    public String retrieveEmailFromToken(String token) throws JWTVerificationException {
        return getDecodedToken(token).getSubject();
    }

    public String[] retrieveRolesFromToken(String token) throws JWTVerificationException{
        return getDecodedToken(token).getClaim(ROLES_TAG).asArray(String.class);
    }

    public void authorizeToken(String token) throws JWTVerificationException, AuthenticationException, JWTTokenBlacklistedException{
        String email = retrieveEmailFromToken(token);
        if (StaticContextHolder.getInstance().jwtTokenExists(email, token)){
            throw new JWTTokenBlacklistedException();
        }else{
            String[] roles = retrieveRolesFromToken(token);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            stream(roles).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role));
            });
            String password = userService.loadUserByUsername(email).getPassword();
            var authenticationToken = new EmailPasswordAuthenticationToken(email, password, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

}
