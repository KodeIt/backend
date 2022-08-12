package com.kodeit.backend.service;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.entity.User;
import com.kodeit.backend.exception.code.CodeException;
import com.kodeit.backend.modal.ExecutionOutput;
import org.springframework.data.domain.Page;

public interface CodeService {

    /*
     * Gets the code by its id
     */
    Code get(Long codeId) throws CodeException;

    /*
     * Gets all the codes till date. To be removed soon
     */
    Page<Code> getAllCodes();

    /*
     * Updates code, input, language of a code
     */
    void updateCode(Long codeId, Code code) throws CodeException;

    /*
     * Deletes a code from the database
     */
    void deleteCode(Long codeId) throws CodeException;

    /*
     * Saves a code to the database
     */
    Long saveCode(Code code);

    /*
     * Runs a code
     */
    ExecutionOutput runCode(Code code) throws CodeException;

    void starCode(Long codeId) throws CodeException;

    void unStarCode(Long codeId) throws CodeException;

    Page<User> getStarredUsers(Long codeId) throws CodeException;

}
