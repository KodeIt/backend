package com.kodeit.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StaticContextHolder {

    private static StaticContextHolder staticContextHolder;
    private static Map<String, List<String>> jwtBlacklist; // <email, token>

    public static StaticContextHolder getInstance() {
        if (staticContextHolder == null) {
            staticContextHolder = new StaticContextHolder();
            jwtBlacklist = Collections.synchronizedMap(new HashMap<>());
        }
        return staticContextHolder;
    }

    public synchronized void addToken(String email, String token) {
        jwtBlacklist.putIfAbsent(email, new ArrayList<>());
        jwtBlacklist.get(email).add(token);
        log.info(String.format("Added jwt token to blacklist for %s", email));
    }

    public synchronized boolean jwtTokenExists(String email, String token) {
        if (jwtBlacklist.containsKey(email))
            return jwtBlacklist.get(email).contains(token);
        return false;
    }

    @Async
    @Bean
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public synchronized void jwtTokenCleanup() {
        log.info("Beginning jwt token cleanup. Current size: " + jwtBlacklist.size());
        jwtBlacklist.forEach(jwtBlacklist::remove);
        log.info("Finished jtw token cleanup. Current size: " + jwtBlacklist.size());
    }

}
