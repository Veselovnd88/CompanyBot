package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.companybot.dto.MessageResponseDTO;
import ru.veselov.companybot.entity.CustomerMessageEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "messageId", source = "messageId")
    @Mapping(target = "text", source = "message.text")
    MessageResponseDTO toMessageDTO(CustomerMessageEntity messageEntity);

    List<MessageResponseDTO> toListOfMessageDTO(List<CustomerMessageEntity> entityList);

}
