package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.CompanyBot.dao.ChatDAO;
import ru.veselov.CompanyBot.entity.ChatEntity;

import java.util.List;

@Service
@Slf4j
public class ChatService {
    private final ChatDAO chatDAO;
    private final ModelMapper modelMapper;
    @Autowired
    public ChatService(ChatDAO chatDAO, ModelMapper modelMapper) {
        this.chatDAO = chatDAO;
        this.modelMapper = modelMapper;
    }

    public void save(Chat chat){
        chatDAO.save(toChatEntity(chat));
        log.info("Канал {} сохранен в БД",chat.getId());
    }

    public void remove(Long chatId){
        chatDAO.deleteById(chatId);
        log.info("Чат {} удален из БД", chatId);
    }

    public List<Chat> findAll(){
        return chatDAO.findAll().stream().map(this::toChat).toList();
    }

    private ChatEntity toChatEntity(Chat chat){
        ChatEntity mapped = modelMapper.map(chat, ChatEntity.class);
        mapped.setChatId(chat.getId());
        return mapped;
    }
    private Chat toChat(ChatEntity chatEntity){
        Chat mapped = modelMapper.map(chatEntity, Chat.class);
        mapped.setId(chatEntity.getChatId());
        return mapped;
    }

}
