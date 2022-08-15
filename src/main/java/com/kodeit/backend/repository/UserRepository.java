package com.kodeit.backend.repository;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT U.followers FROM User U WHERE U.id = :userId")
    Page<User> getFollowers(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT U.following FROM User U WHERE U.id = :userId")
    Page<User> getFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT U FROM User U WHERE :code IN elements(U.codesStarred)")
    Page<User> getStarredUsers(@Param("code")Code code, Pageable pageable);
}
