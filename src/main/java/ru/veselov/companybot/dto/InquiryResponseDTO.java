package ru.veselov.companybot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.model.DivisionModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryResponseDTO implements Serializable {

    private UUID inquiryId;

    private LocalDateTime date;

    private DivisionModel division;

    private final Set<Message> messages = new LinkedHashSet<>();

    private CustomerResponseDTO customer;

}
