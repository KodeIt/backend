package com.kodeit.backend.controller.privateController;

import com.kodeit.backend.entity.User;
import com.kodeit.backend.modal.JWT;
import com.kodeit.backend.security.jwt.JWTTokenBlacklistRepository;
import com.kodeit.backend.security.jwt.JWTTokenUtil;
import com.kodeit.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/private/user")
@Slf4j
public class UserControllerPrivate {

    private final UserService userService;

    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public UserControllerPrivate(UserService userService, JWTTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/")
    public ResponseEntity<?> get() {
        try {
            return ResponseEntity.ok().body(userService.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try{
            return ResponseEntity.ok().body(userService.updateUser(user));
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/access-token")
    public ResponseEntity<?> getAccessTokenFromRefreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            return ResponseEntity.ok().body(jwtTokenUtil.generateAccessTokenFromRefreshToken(refreshToken));
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody JWT jwt) {
        JWTTokenBlacklistRepository.addToBlacklist(jwt.getAccessToken());
        JWTTokenBlacklistRepository.addToBlacklist(jwt.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/remove/{userId}")
    public ResponseEntity<?> removeFollower(@PathVariable("userId") Long userId) {
        try {
            userService.removeFollower(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/following/add/{userId}")
    public ResponseEntity<?> addFollowing(@PathVariable("userId") Long userId) {
        try {
            userService.addFollowing(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/following/remove/{userId}")
    public ResponseEntity<?> removeFollowing(@PathVariable("userId") Long userId) {
        try {
            userService.removeFollowing(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser() {
        try {
            userService.deleteUser();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logo")
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok().body(userService.uploadLogo(file));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
