package ru.veselov.companybot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionDTO {

    @NotEmpty
    @Size(max = 10)
    private String name;

    @NotEmpty
    @Size(max = 45)
    private String description;

}
