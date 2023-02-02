package ru.veselov.CompanyBot.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "division")
public class InquiryModel {
    private DivisionModel division;
    private List<Message> messages= new LinkedList<>();
    private Long userId;
    public InquiryModel(Long userId, DivisionModel division){
        this.userId = userId;
        this.division = division;
    }
    public void addMessage(Message message){
        messages.add(message);    }
}
