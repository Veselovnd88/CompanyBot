package ru.veselov.CompanyBot.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.CompanyBot.entity.ChatEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@Slf4j
public class ChatDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ChatEntity save(ChatEntity chat){
        entityManager.persist(chat);
        return chat;
    }
    public List<ChatEntity> findAll(){
        return entityManager.createQuery(" SELECT c from ChatEntity c ").getResultList();
    }
    public Optional<ChatEntity> findOne(Long chatId){
        ChatEntity chat= entityManager.find(ChatEntity.class, chatId);
        return Optional.ofNullable(chat);
    }

    public void delete(ChatEntity chat){
        entityManager.remove(chat);
    }
    public void deleteById(Long chatId) {
        Optional<ChatEntity> chat = findOne(chatId);
        chat.ifPresent(this::delete);

    }
}
