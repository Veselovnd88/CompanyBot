package ru.veselov.companybot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.veselov.companybot.model.DivisionModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InquiryResponseDTO implements Serializable {

    private UUID inquiryId;

    private LocalDateTime date;

    private DivisionModel division;

    private Set<MessageResponseDTO> messages;

    private CustomerResponseDTO customer;

}
