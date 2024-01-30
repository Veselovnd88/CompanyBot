package ru.veselov.companybot.dto;

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

    private Integer messageId;

    private String text;

}
