package com.kodeit.backend.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JWTTokenBlacklistRepository {

    private static final Set<String> repository = Collections.synchronizedSet(new HashSet<>());

    public static synchronized void addToBlacklist(String token){
        repository.add(token);
        log.info("Token added to repository. Current size: {}", repository.size());
    }

    public static synchronized boolean isTokenBlacklisted(String token){
        return repository.contains(token);
    }

    public static synchronized void deleteExpiredTokens(){
        log.info("Running scan on blacklisted token repository. Current size: {}", repository.size());
        repository.forEach(repository::remove);
        log.info("Token removed from blacklist. Current size: {}", repository.size());
    }

}
