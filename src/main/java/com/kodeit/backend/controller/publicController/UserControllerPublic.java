package com.kodeit.backend.controller.publicController;

import com.kodeit.backend.security.jwt.JWTTokenUtil;
import com.kodeit.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/user")
@Slf4j
public class UserControllerPublic {

    private final UserService userService;

    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public UserControllerPublic(UserService userService, JWTTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.get(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getFollowers(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getFollowing(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/codes/starred")
    public ResponseEntity<?> getCodesStarred(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getCodesStarred(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/codes/written")
    public ResponseEntity<?> getCodesWritten(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getCodesWritten(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
