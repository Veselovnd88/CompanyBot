package ru.veselov.companybot.cache.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.InquiryCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataCacheFacadeImpl implements UserDataCacheFacade {

    private final InquiryCache inquiryCache;

    private final UserStateCache userStateCache;

    private final ContactCache contactCache;


    @Override
    public void clear(Long userId) {
        userStateCache.clear(userId);
        inquiryCache.clear(userId);
        contactCache.clear(userId);
    }

    @Override
    public BotState getUserBotState(Long id) {
        return userStateCache.getUserBotState(id);
    }

    @Override
    public void setUserBotState(Long id, BotState botState) {
        userStateCache.setUserBotState(id, botState);
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
    public void createContact(Long userId) {
        contactCache.createContact(userId);
    }

    @Override
    public ContactModel getContact(Long userId) {
        return contactCache.getContact(userId);
    }

}
