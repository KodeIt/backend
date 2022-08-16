package com.kodeit.backend.service.impl;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.enums.Language;
import com.kodeit.backend.enums.SortBy;
import com.kodeit.backend.exception.code.CodeException;
import com.kodeit.backend.exception.code.CodeNotFoundException;
import com.kodeit.backend.exception.code.UnauthorizedActionException;
import com.kodeit.backend.exception.user.UserNotFoundException;
import com.kodeit.backend.modal.CodeSearchOptions;
import com.kodeit.backend.modal.ExecutionOutput;
import com.kodeit.backend.repository.CodeRepository;
import com.kodeit.backend.repository.UserRepository;
import com.kodeit.backend.service.CodeService;
import com.kodeit.backend.util.CodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;

    private final UserRepository userRepository;

    @Autowired
    public CodeServiceImpl(CodeRepository codeRepository,
                           UserRepository userRepository) {
        this.codeRepository = codeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void setPointsAndLevel(User u) {
        u.setPoints(u.getPoints()+5);
        int start = 10;
        for (int a = 0; a < u.getLevel(); a++) {
            start += start * 2;
        }
        if (start == u.getPoints())
            u.setLevel(u.getLevel()+1);
    }

    private List<Language> getSelectedLanguages(CodeSearchOptions searchOptions) {
        List<Language> languages = List.of(
                Language.C,
                Language.CPP,
                Language.JAVA,
                Language.PYTHON,
                Language.SHELL,
                Language.TYPESCRIPT,
                Language.JAVASCRIPT
        );
        return searchOptions.getLanguages() == null ? languages : searchOptions.getLanguages();
    }

    private PageRequest getPageRequest(CodeSearchOptions searchOptions) {
        String sortBy = "updated";
        if (searchOptions.getSortBy() == SortBy.LANGUAGE)
            sortBy = "language";
        if (searchOptions.getSortBy() == SortBy.TITLE)
            sortBy = "title";
        return PageRequest.of(
                searchOptions.getPageIndex() == null ? 0 : searchOptions.getPageIndex(),
                searchOptions.getPageSize() == null ? 12 : searchOptions.getPageSize(),
                Sort.by(
                        searchOptions.getSortOrder() == null ? Sort.Direction.DESC : searchOptions.getSortOrder(),
                        sortBy
                )
        );
    }

    private Page<Code> setStars(Page<Code> codes) {
        User u = getAuthenticatedUser();
        if (u == null) return codes;
        var newCodes = new ArrayList<Code>();
        for (var code : codes.getContent()) {
            code.setIsStarred(u.getCodesStarred().contains(code));
            newCodes.add(code);
        }
        return new PageImpl<>(newCodes, codes.getPageable(), codes.getTotalElements());
    }

    private User getAuthenticatedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
    }

    @Override
    public Code get(Long codeId) throws CodeException {
        return codeRepository.findById(codeId).orElseThrow(CodeNotFoundException::new);
    }

    @Override
    public Page<Code> getAllCodes(CodeSearchOptions searchOptions) {
        return setStars(codeRepository.findByLanguageInAndTitleContaining(
                getSelectedLanguages(searchOptions),
                searchOptions.getTitle() == null ? "" : searchOptions.getTitle(),
                getPageRequest(searchOptions)
        ));
    }

    @Override
    @Transactional
    public void updateCode(Long codeId, Code code) throws CodeException {
        Code c = get(codeId);
        User u = getAuthenticatedUser();
        if (u.getCodesWritten().contains(c)) {
            c.setInput(code.getInput());
            c.setLanguage(code.getLanguage());
            c.setCode(code.getCode());
            c.setUpdated(new Date());
            c.setTitle(code.getTitle());
            c.setDescription(code.getDescription());
        } else
            throw new UnauthorizedActionException();
    }

    @Override
    @Transactional
    public void deleteCode(Long codeId) throws CodeException {
        var code = get(codeId);
        var user = getAuthenticatedUser();
        if (user.getCodesWritten().contains(code)) {
            code.getStarredUsers().forEach(u -> u.getCodesStarred().remove(code));
            user.getCodesWritten().remove(code);
            codeRepository.delete(code);
        } else
            throw new UnauthorizedActionException();
    }

    @Override
    @Transactional
    public Long saveCode(Code code) {
        User user = getAuthenticatedUser();
        code.setUpdated(new Date());
        code.setUser(user);
        code.setStars(0L);
        code.setStarredUsers(new ArrayList<>());
        setPointsAndLevel(user);
        code = codeRepository.save(code);
        user.getCodesWritten().add(code);
        return code.getId();
    }

    @Override
    public ExecutionOutput runCode(Code code) throws CodeException {
        return CodeRunner.execute(code);
    }

    @Override
    @Transactional
    public void starCode(Long codeId) throws CodeException {
        User u = getAuthenticatedUser();
        Code c = get(codeId);

        if (!u.getCodesStarred().contains(c)) {
            u.getCodesStarred().add(c);
            c.setStars(c.getStars() + 1);
        }
    }

    @Override
    @Transactional
    public void unStarCode(Long codeId) throws CodeException {
        User u = getAuthenticatedUser();
        Code c = get(codeId);

        if (u.getCodesStarred().contains(c)) {
            u.getCodesStarred().remove(c);
            c.setStars(c.getStars() - 1);
        }
    }


    @Override
    public Integer getCodesStarredLength() {
        return getAuthenticatedUser().getCodesStarred().size();
    }

    @Override
    public Integer getCodesWrittenLength() {
        return getAuthenticatedUser().getCodesWritten().size();
    }

    @Override
    public Page<Code> getCodesStarred(Long userId, CodeSearchOptions searchOptions) throws UserNotFoundException {
        return setStars(codeRepository.findByLanguageInAndTitleContainingAndIdIn(
                getSelectedLanguages(searchOptions),
                searchOptions.getTitle() == null ? "" : searchOptions.getTitle(),
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getCodesStarred().stream().map(Code::getId).toList(),
                getPageRequest(searchOptions)
        ));
    }

    @Override
    public Page<Code> getCodesWritten(Long userId, CodeSearchOptions searchOptions) throws UserNotFoundException {
        return setStars(codeRepository.findByLanguageInAndTitleContainingAndUserIs(
                getSelectedLanguages(searchOptions),
                searchOptions.getTitle() == null ? "" : searchOptions.getTitle(),
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new),
                getPageRequest(searchOptions)
        ));
    }

}
