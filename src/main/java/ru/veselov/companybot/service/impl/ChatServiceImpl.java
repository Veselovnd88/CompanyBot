package ru.veselov.companybot.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.repository.ChatRepository;
import ru.veselov.companybot.entity.ChatEntity;
import ru.veselov.companybot.service.ChatService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void save(Chat chat) {
        chatRepository.save(toChatEntity(chat));
        log.info("Channel [with id: {}] saved to db", chat.getId());
    }

    @Override
    public void remove(Long chatId) {
        chatRepository.deleteById(chatId);
        log.info("Channel [with id: {}] deleted", chatId);
    }

    @Override
    public List<Chat> findAll() {
        List<Chat> list = chatRepository.findAll().stream().map(this::toChat).toList();
        log.info("Channels retrieved from repository");
        return list;
    }

    //TODO to mapper
    private ChatEntity toChatEntity(Chat chat) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setTitle(chat.getTitle());
        chatEntity.setType(chat.getType());
        chatEntity.setChatId(chat.getId());
        return chatEntity;
    }

    private Chat toChat(ChatEntity chatEntity) {
        Chat chat = new Chat();
        chat.setId(chatEntity.getChatId());
        chat.setType(chatEntity.getType());
        chat.setTitle(chatEntity.getTitle());
        return chat;
    }

}
