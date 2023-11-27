package ru.veselov.CompanyBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

}
