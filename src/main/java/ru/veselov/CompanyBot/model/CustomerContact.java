package ru.veselov.CompanyBot.model;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Contact;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerContact {
    private String lastName;
    private String firstName;
    private String secondName;
    private String phone;
    private String email;
    private Contact contact;
    private Long userId;
}
