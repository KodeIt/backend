package com.kodeit.backend.service;

import com.kodeit.backend.entity.User;
import com.kodeit.backend.exception.code.CodeException;
import com.kodeit.backend.exception.user.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService {

    /*
     * Returns true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /*
     * Gets user by their email. Meant to be used on server side only.
     */
    User get(String email) throws UserNotFoundException;

    /*
     * Gets user by their id. Meant to be used by clients.
     */
    User get(Long userId) throws UserNotFoundException;

    /*
     * Gets the currently logged-in user
     */
    User get() ;

    /*
     * Creates a user after OAuth2 authentication based on name, email and avatar
     */
    void save(String name, String email, String avatar);

    /*
     * Gets the followers of the user referred
     */
    Page<User> getFollowers(Long userId, Integer pageIndex) throws UserNotFoundException;

    /*
     * Gets the following of the user referred
     */
    Page<User> getFollowing(Long userId, Integer pageIndex) throws UserNotFoundException;

    Integer getFollowersCount(Long userId) throws UserNotFoundException;

    Integer getFollowingCount(Long userId) throws UserNotFoundException;

    /*
     * Adds the given user to the following list of
     * the current user
     */
    void follow(Long userId) throws UserNotFoundException;

    /*
     * Removes the given user from the following list of
     * the current user
     */
    void unfollow(Long userId) throws UserNotFoundException;

    /*
     * Updates user specific details
     */
    User updateUser(User user);

    /*
     * Permanently deletes the user and all their data
     */
    void deleteUser();

    String uploadLogo(MultipartFile file) throws IOException;

    Page<User> getStarredUsers(Long codeId, Integer pageIndex) throws CodeException;

}
