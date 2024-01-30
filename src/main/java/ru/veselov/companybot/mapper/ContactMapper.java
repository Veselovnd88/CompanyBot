package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.companybot.dto.ContactResponseDto;
import ru.veselov.companybot.entity.ContactEntity;
import ru.veselov.companybot.model.ContactModel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactMapper {

    ContactModel toModel(ContactEntity contact);

    ContactEntity toEntity(ContactModel contactModel);

    @Mapping(target = "contactId", source = "contactId")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "email", source = "email")
    ContactResponseDto toContactDTO(ContactEntity contact);

}
