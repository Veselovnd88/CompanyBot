package ru.veselov.CompanyBot.model;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedList;
import java.util.List;
@Getter
@Setter
public class CustomerInquiry {
    private Department department;
    private List<Message> messages= new LinkedList<>();
    private User user;
}
