package ru.veselov.companybot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Contact;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactModel {

    private String lastName;

    private String firstName;

    private String secondName;

    private String phone;

    private String email;

    private Contact contact;

    private Long userId;

}
