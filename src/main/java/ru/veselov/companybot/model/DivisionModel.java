package ru.veselov.companybot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"inquiries"})
@AllArgsConstructor
@Builder
public class DivisionModel {

    private Long divisionId;

    private String name;

    @Builder.Default
    private final Set<InquiryModel> inquiries = new HashSet<>();

}
