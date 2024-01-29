package ru.veselov.companybot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link ru.veselov.companybot.entity.ContactEntity}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDto implements Serializable {

    private UUID contactId;

    private String phone;

    private String email;

}
