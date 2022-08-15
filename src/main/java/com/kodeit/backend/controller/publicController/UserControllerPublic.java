package com.kodeit.backend.controller.publicController;

import com.kodeit.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user")
@Slf4j
public class UserControllerPublic {

    private final UserService userService;

    @Autowired
    public UserControllerPublic(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<?> getFollowers(@PathVariable("userId") Long userId, @RequestParam("pageIndex") Integer pageIndex) {
        try {
            return ResponseEntity.ok().body(userService.getFollowers(userId, pageIndex));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable("userId") Long userId, @RequestParam("pageIndex") Integer pageIndex) {
        try {
            return ResponseEntity.ok().body(userService.getFollowing(userId, pageIndex));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<?> getFollowersCount(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getFollowersCount(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<?> getFollowingCount(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(userService.getFollowingCount(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{codeId}/stars")
    public ResponseEntity<?> getStars(@PathVariable("codeId") Long codeId, @RequestParam("pageIndex") Integer pageIndex) {
        try {
            return ResponseEntity.ok().body(userService.getStarredUsers(codeId, pageIndex));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

}
