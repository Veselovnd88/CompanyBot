package ru.veselov.companybot.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.mapper.ChatMapper;
import ru.veselov.companybot.repository.ChatRepository;
import ru.veselov.companybot.service.ChatService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    @Override
    @Transactional
    public void save(Chat chat) {
        chatRepository.save(chatMapper.toEntity(chat));
        log.info("Channel [with id: {}] saved to db", chat.getId());
    }

    @Override
    public void remove(Long chatId) {
        chatRepository.deleteById(chatId);
        log.info("Channel [with id: {}] deleted", chatId);
    }

    @Override
    public List<Chat> findAll() {
        List<Chat> list = chatMapper.toListModels(chatRepository.findAll());
        log.info("Channels retrieved from repository");
        return list;
    }

}
