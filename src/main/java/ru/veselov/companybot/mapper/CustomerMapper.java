package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.entity.CustomerEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    CustomerEntity toEntity(User user);

}
