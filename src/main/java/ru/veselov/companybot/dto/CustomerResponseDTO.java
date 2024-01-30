package ru.veselov.companybot.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ru.veselov.companybot.entity.CustomerEntity}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO implements Serializable {
    @Schema(description = "Id клиента", example = "1000")
    private Long id;

    @Schema(description = "Имя клиента", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Петров")
    private String lastName;

    @Schema(description = "Юзернейм клиента", example = "Ivan")
    private String userName;

    @ArraySchema(arraySchema = @Schema(implementation = ContactResponseDto.class, description = "Контакты"))
    private Set<ContactResponseDto> contacts;

}
