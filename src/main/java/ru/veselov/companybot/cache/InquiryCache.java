package ru.veselov.companybot.cache;

import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

public interface InquiryCache extends Cache {

    void createInquiry(Long userId, DivisionModel division);

    InquiryModel getInquiry(Long userId);

}
