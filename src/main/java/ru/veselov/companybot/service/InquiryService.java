package ru.veselov.companybot.service;

import org.springframework.data.domain.Page;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.dto.PagingParams;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;

public interface InquiryService {

    InquiryResponseDTO save(InquiryModel inquiry);

    Page<InquiryResponseDTO> findAll(PagingParams pagingParams);

}
