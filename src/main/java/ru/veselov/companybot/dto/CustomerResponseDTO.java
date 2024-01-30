package ru.veselov.companybot.dto;

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

    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    private Set<ContactResponseDto> contacts;

}
