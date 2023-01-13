package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

}
