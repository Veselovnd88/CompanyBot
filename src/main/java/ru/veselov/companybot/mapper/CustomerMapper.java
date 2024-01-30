package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.dto.CustomerResponseDTO;
import ru.veselov.companybot.entity.CustomerEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ContactMapper.class)
public interface CustomerMapper {

    CustomerEntity toEntity(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userName", source = "userName")
    CustomerResponseDTO toCustomerDTO(CustomerEntity customerEntity);

}
