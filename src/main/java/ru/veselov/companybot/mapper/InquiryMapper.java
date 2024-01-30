package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import ru.veselov.companybot.dto.InquiryResponseDTO;
import ru.veselov.companybot.entity.InquiryEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CustomerMapper.class, MessageMapper.class})
public interface InquiryMapper {

    InquiryResponseDTO entityToDTO(InquiryEntity inquiryEntity);

    default Page<InquiryResponseDTO> entitiesToDTOS(Page<InquiryEntity> entities) {
        return entities.map(this::entityToDTO);
    }

}
