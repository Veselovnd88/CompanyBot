package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.companybot.entity.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

}
