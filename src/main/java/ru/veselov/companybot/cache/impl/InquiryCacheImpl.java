package ru.veselov.companybot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.cache.InquiryCache;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for storing temporary inquiry data of user(customer), customer will fill it in several steps/commands
 */
@Component
@Slf4j
public class InquiryCacheImpl implements InquiryCache {

    private final Map<Long, InquiryModel> inquiryMap = new ConcurrentHashMap<>();

    @Override
    public void createInquiry(Long userId, DivisionModel division) {
        InquiryModel inquiryModel = new InquiryModel(userId, division);
        inquiryMap.put(userId, inquiryModel);
        log.debug("For [user id: {}] inquiry object was created", userId);
    }

    @Override
    public InquiryModel getInquiry(Long userId) {
        InquiryModel inquiryModel = inquiryMap.get(userId);
        log.debug("Inquiry for [user: id {}] retrieved from repo", userId);
        return inquiryModel;
    }

    @Override
    public void clear(Long userId) {
        inquiryMap.remove(userId);
        log.debug("Inquiry of [user id: {}] was removed from cache", userId);
    }

}
