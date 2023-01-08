package ru.veselov.CompanyBot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CustomerInquiry {
    private Department department;
    private List<Message> messages= new LinkedList<>();
    private Long userId;

    public CustomerInquiry(Long userId, Department department){
        this.userId = userId;
        this.department = department;
    }
    public void addMessage(Message message){
        messages.add(message);
    }
}
