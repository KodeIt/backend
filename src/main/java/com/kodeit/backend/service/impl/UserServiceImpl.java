package com.kodeit.backend.service.impl;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.exception.user.UserNotFoundException;
import com.kodeit.backend.repository.CodeRepository;
import com.kodeit.backend.repository.CountryRepository;
import com.kodeit.backend.repository.StateRepository;
import com.kodeit.backend.repository.UserRepository;
import com.kodeit.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CodeRepository codeRepository;

    private final StateRepository stateRepository;

    private final CountryRepository countryRepository;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            CodeRepository codeRepository,
            StateRepository stateRepository,
            CountryRepository countryRepository
    ) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
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
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
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
    public Page<User> getFollowers(Long userId) throws UserNotFoundException {
        return userRepository.getFollowers(userId, PageRequest.of(0, 10));
    }

    @Override
    public Page<User> getFollowing(Long userId) throws UserNotFoundException {
        return userRepository.getFollowing(userId, PageRequest.of(0, 10));
    }

    @Override
    public Page<Code> getCodesStarred(Long userId) throws UserNotFoundException {
        return codeRepository.getCodesStarred(userId, PageRequest.of(0, 10));
    }

    @Override
    public Page<Code> getCodesWritten(Long userId) throws UserNotFoundException {
        return codeRepository.getCodesWritten(userId, PageRequest.of(0, 10));
    }

    @Override
    @Transactional
    public void addFollower(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        if(user.getFollowers().stream().noneMatch(u -> Objects.equals(u.getId(), userId)))
            user.getFollowers().add(get(userId));
    }

    @Override
    @Transactional
    public void removeFollower(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        if(user.getFollowers().stream().noneMatch(u -> Objects.equals(u.getId(), userId)))
            user.getFollowers().remove(get(userId));
    }

    @Override
    @Transactional
    public void addFollowing(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        if(user.getFollowing().stream().noneMatch(u -> Objects.equals(u.getId(), userId)))
            user.getFollowing().add(get(userId));
    }

    @Override
    @Transactional
    public void removeFollowing(Long userId) throws UserNotFoundException {
        User user = getAuthenticatedUser();
        if(user.getFollowing().stream().noneMatch(u -> Objects.equals(u.getId(), userId)))
            user.getFollowing().remove(get(userId));
    }

    @Override
    @Transactional
    public void updateUser(User u) {
        User user = getAuthenticatedUser();
        if (u.getAvatar() != null) user.setAvatar(u.getAvatar());
        if (u.getBio() != null) user.setBio(u.getBio());
        if (u.getName() != null) user.setName(u.getName());
        if (u.getCountry().getId() != null) user.setCountry(countryRepository.findById(u.getCountry().getId()).orElse(null));
        if (u.getState().getId() != null) user.setState(stateRepository.findById(u.getState().getId()).orElse(null));
    }

    @Override
    @Transactional
    public void deleteUser() {
        userRepository.delete(getAuthenticatedUser());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return get(email);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
