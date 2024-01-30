package ru.veselov.companybot.service;

import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;

public interface InquiryService {

    InquiryResponseDTO save(InquiryModel inquiry);

    List<InquiryResponseDTO> findAll();

}
