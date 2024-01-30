package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.entity.InquiryEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MessageMapper.class, CustomerMapper.class})
public interface InquiryMapper {

    @Mapping(target = "customer.id", source = "customer.id")
    InquiryResponseDTO entityToDTO(InquiryEntity inquiryEntity);

    List<InquiryResponseDTO> entitiesToDTOS(List<InquiryEntity> entities);

}
