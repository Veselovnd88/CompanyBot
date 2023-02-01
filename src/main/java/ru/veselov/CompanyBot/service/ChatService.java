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
    @Autowired
    public ChatService(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    public void save(Chat chat){
        chatDAO.save(toChatEntity(chat));
        log.info("{}: канал сохранен в БД",chat.getId());
    }

    public void remove(Long chatId){
        chatDAO.deleteById(chatId);
        log.info("{}: канал удален из БД", chatId);
    }

    public List<Chat> findAll(){
        return chatDAO.findAll().stream().map(this::toChat).toList();
    }

    private ChatEntity toChatEntity(Chat chat){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setTitle(chat.getTitle());
        chatEntity.setType(chat.getType());
        chatEntity.setChatId(chat.getId());
        return chatEntity;
    }
    private Chat toChat(ChatEntity chatEntity){
        Chat chat = new Chat();
        chat.setId(chatEntity.getChatId());
        chat.setType(chatEntity.getType());
        chat.setTitle(chatEntity.getTitle());
        return chat;
    }
}
