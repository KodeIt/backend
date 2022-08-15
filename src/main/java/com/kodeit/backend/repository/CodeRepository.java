package com.kodeit.backend.repository;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    Page<Code> findByLanguageInAndTitleContaining(List<Language> languages, String title, Pageable pageable);

    Page<Code> findByLanguageInAndTitleContainingAndIdIn(List<Language> languages, String title, List<Long> ids, Pageable pageable);

    Page<Code> findByLanguageInAndTitleContainingAndUserIs(List<Language> languages, String title, User user, Pageable pageable);

}
