package ru.veselov.companybot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id контакта", example = "8b19a7cf-67f2-47b3-9ddb-9e3d9514d375")
    private UUID contactId;

    @Schema(description = "Телефонный номер", example = "+7 916 555 55 55")
    private String phone;

    @Schema(description = "E-mail", example = "email@email.com")
    private String email;

}
