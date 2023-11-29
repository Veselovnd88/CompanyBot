package ru.veselov.companybot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.entity.ChatEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {

    ChatEntity toEntity(Chat chat);

    Chat toModel(ChatEntity chatEntity);

    List<Chat> toListModels(List<ChatEntity> chatEntities);

}
