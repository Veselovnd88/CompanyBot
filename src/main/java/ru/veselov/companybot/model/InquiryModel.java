package ru.veselov.companybot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "division")
public class InquiryModel {

    private DivisionModel division;

    private List<Message> messages = new LinkedList<>();

    private Long userId;

    public InquiryModel(Long userId, DivisionModel division) {
        this.userId = userId;
        this.division = division;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

}
