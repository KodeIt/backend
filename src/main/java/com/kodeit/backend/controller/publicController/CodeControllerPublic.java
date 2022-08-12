package com.kodeit.backend.controller.publicController;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/code")
@Slf4j
@CrossOrigin
public class CodeControllerPublic {

    private final CodeService codeService;

    @Autowired
    public CodeControllerPublic(CodeService codeService) {
        this.codeService = codeService;
    }

    @GetMapping("/{codeId}")
    public ResponseEntity<?> get(@PathVariable("codeId") Long codeId) {
        try{
            return ResponseEntity.ok().body(codeService.get(codeId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        try{
            return ResponseEntity.ok().body(codeService.getAllCodes());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody Code code) {
        try{
            return ResponseEntity.ok().body(codeService.runCode(code));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{codeId}/stars")
    public ResponseEntity<?> getStars(@PathVariable("codeId") Long codeId) {
        try {
            codeService.getStarredUsers(codeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

}
