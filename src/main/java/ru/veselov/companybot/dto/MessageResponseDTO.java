package ru.veselov.companybot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link org.telegram.telegrambots.meta.api.objects.Message}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO implements Serializable {

    @Schema(description = "Id сообщения", example = "3b4cb719-3489-445d-bb01-ef7958aca896")
    private Integer messageId;

    @Schema(description = "Текст сообщения", example = "Перезвоните мне")
    private String text;

}
