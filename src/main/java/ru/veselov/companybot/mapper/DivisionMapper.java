package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.model.DivisionModel;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DivisionMapper {

    @Mapping(target = "inquiries", ignore = true)
    DivisionModel toModel(DivisionEntity divisionEntity);

    List<DivisionModel> toListModel(List<DivisionEntity> entities);

    DivisionEntity toEntity(DivisionModel divisionModel);

}
