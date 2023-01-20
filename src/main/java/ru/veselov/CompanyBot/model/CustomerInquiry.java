package ru.veselov.CompanyBot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.entity.Division;

import java.util.LinkedList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CustomerInquiry {
    private Division division;
    private List<Message> messages= new LinkedList<>();
    private Long userId;

    public CustomerInquiry(Long userId, Division division){
        this.userId = userId;
        this.division = division;
    }
    public void addMessage(Message message){
        messages.add(message);
    }
}
