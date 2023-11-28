package ru.veselov.companybot.service;

import org.springframework.transaction.annotation.Transactional;
import ru.veselov.companybot.entity.Inquiry;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.Optional;

public interface InquiryService {
    @Transactional
    Inquiry save(InquiryModel inquiry);

    Optional<Inquiry> findWithMessages(Integer id);

    List<Inquiry> findAll();
}
