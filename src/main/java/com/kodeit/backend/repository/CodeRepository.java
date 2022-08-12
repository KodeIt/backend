package com.kodeit.backend.repository;

import com.kodeit.backend.entity.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    @Query(value = "SELECT U.codesStarred FROM User U WHERE U.id = :userId")
    Page<Code> getCodesStarred(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT U.codesWritten FROM User U WHERE U.id = :userId")
    Page<Code> getCodesWritten(@Param("userId") Long userId, Pageable pageable);

}
