package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.entity.InquiryEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InquiryMapper {

    InquiryResponseDTO entityToDTO(InquiryEntity inquiryEntity);

    List<InquiryResponseDTO> entitiesToDTOS(List<InquiryEntity> entities);

}
