package ru.veselov.companybot.service;

import ru.veselov.companybot.entity.InquiryEntity;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InquiryService {

    InquiryEntity save(InquiryModel inquiry);

    Optional<InquiryEntity> findWithMessages(UUID id);

    List<InquiryEntity> findAll();

}
