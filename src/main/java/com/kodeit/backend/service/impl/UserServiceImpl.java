package com.kodeit.backend.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.exception.code.CodeException;
import com.kodeit.backend.exception.code.CodeNotFoundException;
import com.kodeit.backend.exception.user.UserNotFoundException;
import com.kodeit.backend.repository.CodeRepository;
import com.kodeit.backend.repository.CountryRepository;
import com.kodeit.backend.repository.StateRepository;
import com.kodeit.backend.repository.UserRepository;
import com.kodeit.backend.service.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    private final UserRepository userRepository;

    private final CodeRepository codeRepository;

    private final StateRepository stateRepository;

    private final CountryRepository countryRepository;

    @Autowired
    public UserServiceImpl(
            AmazonS3 amazonS3,
            UserRepository userRepository,
            CodeRepository codeRepository,
            StateRepository stateRepository,
            CountryRepository countryRepository
    ) {
        this.amazonS3 = amazonS3;
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    private Page<User> setFollowing(Page<User> users) {
        User u = getAuthenticatedUser();
        if (u == null)
            return users;

        List<User> updatedUsers = new ArrayList<>();
        for (var i : users.getContent()) {
            if (u.getFollowing().contains(i))
                i.setIsFollowing(true);
            updatedUsers.add(i);
        }
        return new PageImpl<>(updatedUsers, users.getPageable(), users.getSize());
    }

    private User getAuthenticatedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User get(@NotNull String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User get(Long userId) throws UserNotFoundException {
        User a = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User b = getAuthenticatedUser();
        if (b != null)
            a.setIsFollowing(b.getFollowers().contains(a));
        return a;
    }

    @Override
    public User get() {
        return getAuthenticatedUser();
    }

    @Override
    @Transactional
    public void save(String name, String email, String avatar) {
        User u = new User(
                null,
                email,
                avatar,
                name,
                null,
                0L,
                0L,
                new Date(),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );
        userRepository.save(u);
    }

    @Override
    public Page<User> getFollowers(Long userId, Integer pageIndex) throws UserNotFoundException {
        return setFollowing(userRepository.getFollowers(userId, PageRequest.of(pageIndex == null ? 0 : pageIndex, 10)));
    }

    @Override
    public Page<User> getFollowing(Long userId, Integer pageIndex) throws UserNotFoundException {
        return setFollowing(userRepository.getFollowing(userId, PageRequest.of(pageIndex == null ? 0 : pageIndex, 10)));
    }

    @Override
    public Integer getFollowersCount(Long userId) throws UserNotFoundException {
        return get(userId).getFollowers().size();
    }

    @Override
    public Integer getFollowingCount(Long userId) throws UserNotFoundException {
        return get(userId).getFollowing().size();
    }

    @Override
    @Transactional
    public void removeFollower(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        User u2 = get(userId);
        if(user.getFollowers().contains(u2)) {
            user.getFollowers().remove(u2);
            u2.getFollowing().remove(user);
        }
    }

    @Override
    @Transactional
    public void addFollowing(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        User u2 = get(userId);

        // Can't follow self
        if (u2.equals(user))
            return;

        if(!user.getFollowing().contains(u2)) {
            user.getFollowing().add(u2);
            u2.getFollowers().add(user);
        }
    }

    @Override
    @Transactional
    public void removeFollowing(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        User u2 = get(userId);
        if(user.getFollowing().contains(u2)) {
            user.getFollowing().remove(u2);
            u2.getFollowers().remove(user);
        }
    }

    @Override
    @Transactional
    public User updateUser(User u) {
        User user = getAuthenticatedUser();
        if (u.getAvatar() != null) user.setAvatar(u.getAvatar());
        if (u.getBio() != null) user.setBio(u.getBio());
        if (u.getName() != null) user.setName(u.getName());
        if (u.getCountry().getId() != null) user.setCountry(countryRepository.findById(u.getCountry().getId()).orElse(null));
        if (u.getState().getId() != null) user.setState(stateRepository.findById(u.getState().getId()).orElse(null));
        return user;
    }

    @Override
    @Transactional
    public void deleteUser() {
        userRepository.delete(getAuthenticatedUser());
    }

    @Override
    @Transactional
    public String uploadLogo(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            User u = getAuthenticatedUser();
            File f = new File(Objects.requireNonNull(file.getOriginalFilename()));
            FileOutputStream fo = new FileOutputStream(f);
            IOUtils.copy(file.getInputStream(), fo);
            amazonS3.putObject(bucketName, "public/user_logo/" + u.getId() + ".png", f);
            String url = "https://kodeit.s3.ap-south-1.amazonaws.com/public/user_logo/"+u.getId()+".png";
            u.setAvatar(url);
            return url;
        }
        return "";
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return get(email);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public Page<User> getStarredUsers(Long codeId, Integer pageIndex) throws CodeException {
        Code code = codeRepository.findById(codeId).orElseThrow(CodeNotFoundException::new);
        return setFollowing(userRepository.getStarredUsers(code, PageRequest.of(pageIndex == null ? 0 : pageIndex, 10)));
    }

}
