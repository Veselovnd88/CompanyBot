package ru.veselov.companybot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
public class ChatEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column
    private String title;

    @Column
    private String type;

}
