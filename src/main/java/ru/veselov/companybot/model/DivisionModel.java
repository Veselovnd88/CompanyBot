package ru.veselov.companybot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"inquiries"})
@AllArgsConstructor
@Builder
public class DivisionModel implements Serializable {

    @Schema(description = "Id of division", example = "3b4cb719-3489-445d-bb01-ef7958aca896")
    private UUID divisionId;

    @Schema(description = "Short name of division", example = "Common")
    private String name;

    @Schema(description = "Description of division", example = "Common questions here")
    private String description;

    @JsonIgnore
    @Builder.Default
    private final Set<InquiryModel> inquiries = new HashSet<>();

}
