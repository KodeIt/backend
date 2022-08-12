package com.kodeit.backend.service.impl;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.exception.code.CodeException;
import com.kodeit.backend.exception.code.CodeNotFoundException;
import com.kodeit.backend.modal.ExecutionOutput;
import com.kodeit.backend.repository.CodeRepository;
import com.kodeit.backend.repository.UserRepository;
import com.kodeit.backend.service.CodeService;
import com.kodeit.backend.util.CodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

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

    private User getAuthenticatedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
    }

    @Override
    public Code get(Long codeId) throws CodeException {
        return codeRepository.findById(codeId).orElseThrow(CodeNotFoundException::new);
    }

    @Override
    public Page<Code> getAllCodes() {
        return codeRepository.findAll(PageRequest.of(0, 10));
    }

    @Override
    @Transactional
    public void updateCode(Long codeId, Code code) throws CodeException {
        Code c = get(codeId);
        c.setInput(code.getInput());
        c.setLanguage(code.getLanguage());
        c.setCode(code.getCode());
        c.setUpdated(new Date());
    }

    @Override
    @Transactional
    public void deleteCode(Long codeId) throws CodeException {
        var code = get(codeId);
        var user = getAuthenticatedUser();
        user.getCodesWritten().remove(code);
    }

    @Override
    @Transactional
    public Long saveCode(Code code) {
        User user = getAuthenticatedUser();
        code.setUpdated(new Date());
        code.setUser(user);
        code.setStars(0L);
        code = codeRepository.save(code);
        user.getCodesWritten().add(code);
        return code.getId();
    }

    @Override
    public ExecutionOutput runCode(Code code) throws CodeException{
        return CodeRunner.execute(code);
    }

    @Override
    @Transactional
    public void starCode(Long codeId) throws CodeException {
        User u = getAuthenticatedUser();
        Code c = get(codeId);

        if (!u.getCodesStarred().contains(c)) {
            u.getCodesStarred().add(c);
            c.setStars(c.getStars()+1);
        }
    }

    @Override
    @Transactional
    public void unStarCode(Long codeId) throws CodeException {
        User u = getAuthenticatedUser();
        Code c = get(codeId);

        if (u.getCodesStarred().contains(c)) {
            u.getCodesStarred().remove(c);
            c.setStars(c.getStars()-1);
        }
    }

    @Override
    public Page<User> getStarredUsers(Long codeId) throws CodeException {
        return userRepository.getStarredUsers(get(codeId), PageRequest.of(0, 10));
    }

}
