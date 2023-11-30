package ru.veselov.companybot.cache.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.cache.InquiryCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataCacheImpl implements UserDataCache {

    private final InquiryCache inquiryCache;

    private final Map<Long, BotState> currentUserBotState = new ConcurrentHashMap<>();

    @Override
    public BotState getUserBotState(Long id) {
        return currentUserBotState.computeIfAbsent(id, k -> BotState.BEGIN);
    }

    @Override
    public void setUserBotState(Long id, BotState botState) {
        currentUserBotState.put(id, botState);
        log.info("For [user id: {}] was set [bot state: {}]", id, botState);
    }

    @Override
    public void createInquiry(Long userId, DivisionModel division) {
        inquiryCache.createInquiry(userId, division);
    }

    @Override
    public InquiryModel getInquiry(Long userId) {
        return inquiryCache.getInquiry(userId);
    }

    @Override
    public void clear(Long userId) {
        inquiryCache.clear(userId);
        currentUserBotState.put(userId, BotState.READY);
        log.debug("For [user id: {}] set [bot state is: {}]", userId, BotState.READY);
    }

}
