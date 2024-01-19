package ru.veselov.companybot.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationError {

    private String name;

    private String message;

    private String currentValue;

}