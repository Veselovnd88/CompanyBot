package ru.veselov.CompanyBot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.model.InquiryModel;

import java.util.HashMap;

@Component
@Slf4j
public class UserDataCacheImpl implements UserDataCache {

    private final HashMap<Long, BotState> currentUserBotState = new HashMap<>();
    private final HashMap<Long, InquiryModel> inquiryCache = new HashMap<>();

    @Override
    public BotState getUserBotState(Long id) {
        BotState botState = currentUserBotState.get(id);
        if(botState==null){
            botState=BotState.BEGIN;
            currentUserBotState.put(id,botState);
        }
        return botState;
    }

    @Override
    public void setUserBotState(Long id,BotState botState) {
        log.info("{}: установлен статус бота {} для пользователя",id,botState);
        currentUserBotState.put(id,botState);
    }

    @Override
    public void createInquiry(Long userId, DivisionModel division) {
        log.info("{}: создан объект Inquiry для пользователя ", userId);
        InquiryModel inquiryModel = new InquiryModel(userId,division);
        inquiryCache.put(userId, inquiryModel);
    }

    @Override
    public InquiryModel getInquiry(Long userId) {
        return inquiryCache.get(userId);
    }

    @Override
    public void clear(Long userId) {
        inquiryCache.remove(userId);
        currentUserBotState.put(userId,BotState.READY);
        log.info("{}: запрос пользователя  удален из кеша, статус переведен в {}",userId,BotState.READY);
    }
}
