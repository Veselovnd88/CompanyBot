package ru.veselov.companybot.service;

import ru.veselov.companybot.entity.Inquiry;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.Optional;

public interface InquiryService {

    Inquiry save(InquiryModel inquiry);

    Optional<Inquiry> findWithMessages(Long id);

    List<Inquiry> findAll();

}
