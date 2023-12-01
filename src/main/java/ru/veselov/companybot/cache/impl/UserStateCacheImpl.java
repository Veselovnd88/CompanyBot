package ru.veselov.companybot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.cache.UserStateCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for storing BotState for every user
 */
@Component
@Slf4j
public class UserStateCacheImpl implements UserStateCache {

    private final Map<Long, BotState> currentUserBotState = new ConcurrentHashMap<>();

    /**
     * Clear here reset BotState to basic state READY
     */
    @Override
    public void clear(Long userId) {
        currentUserBotState.put(userId, BotState.READY);
        log.debug("Reset of bot state for [user id: {}, default state: {}]", userId, BotState.READY);
    }

    /**
     * With the first communication with bot BotState BEGIN setting up
     */
    @Override
    public BotState getUserBotState(Long id) {
        BotState botState = currentUserBotState.computeIfAbsent(id, k -> BotState.BEGIN);
        log.debug("Current [user id: {}] [bot state is {}]", id, botState);
        return botState;
    }

    @Override
    public void setUserBotState(Long id, BotState botState) {
        currentUserBotState.put(id, botState);
        log.info("For [user id: {}] was set [bot state: {}]", id, botState);
    }

}
