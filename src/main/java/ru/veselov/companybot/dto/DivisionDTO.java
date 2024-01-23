package ru.veselov.companybot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionDTO {

    @Schema(description = "Short name of division", example = "Common")
    @NotEmpty
    @Size(max = 10)
    private String name;

    @Schema(description = "Description of division", example = "Common questions here")
    @NotEmpty
    @Size(max = 45)
    private String description;

}
