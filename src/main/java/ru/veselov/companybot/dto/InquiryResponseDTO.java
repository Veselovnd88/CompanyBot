package ru.veselov.companybot.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id запроса", example = "d15a43eb-0bb9-4b15-ac3a-abdf3b77137f")
    private UUID inquiryId;

    @Schema(description = "Дата запроса", example = "130f4ee4-037f-47b3-9b63-8c3b0ac02574")
    private LocalDateTime date;

    @Schema(implementation = DivisionModel.class, description = "Отдел")
    private DivisionModel division;

    @ArraySchema(arraySchema = @Schema(implementation = MessageResponseDTO.class, description = "Сообщения"))
    private Set<MessageResponseDTO> messages;

    @Schema(implementation = CustomerResponseDTO.class, description = "Клиент")
    private CustomerResponseDTO customer;

}
