package ru.veselov.CompanyBot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerInquiry;
import ru.veselov.CompanyBot.model.Department;

import java.util.HashMap;

@Component
@Slf4j
public class UserDataCacheImpl implements UserDataCache {

    private HashMap<Long, BotState> currentUserBotState = new HashMap<>();
    private HashMap<Long, CustomerInquiry> inquiryCache = new HashMap<>();
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
        currentUserBotState.put(id,botState);
    }

    @Override
    public void createInquiry(Long userId, Department department) {
        log.info("Создан объект Inquiry для пользователя {}", userId);
        CustomerInquiry customerInquiry = new CustomerInquiry(userId,department);
        inquiryCache.put(userId,customerInquiry);
    }

    @Override
    public CustomerInquiry getInquiry(Long userId) {
        return inquiryCache.get(userId);
    }
}
