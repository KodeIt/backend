package com.kodeit.backend.controller.privateController;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private/code")
@Slf4j
public class CodeControllerPrivate {

    private final CodeService codeService;

    @Autowired
    public CodeControllerPrivate(CodeService codeService) {
        this.codeService = codeService;
    }

    @PutMapping("/{codeId}")
    public ResponseEntity<?> updateCode(@PathVariable("codeId") Long codeId, @RequestBody Code code) {
        try {
            codeService.updateCode(codeId, code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{codeId}")
    public ResponseEntity<?> deleteCode(@PathVariable("codeId") Long codeId) {
        try {
            codeService.deleteCode(codeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> saveCode(@RequestBody Code code) {
        try {
            return ResponseEntity.ok().body(codeService.saveCode(code));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{codeId}/star")
    public ResponseEntity<?> starCode(@PathVariable("codeId") Long codeId) {
        try {
            codeService.starCode(codeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{codeId}/un-star")
    public ResponseEntity<?> unStarCode(@PathVariable("codeId") Long codeId) {
        try {
            codeService.unStarCode(codeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

}
