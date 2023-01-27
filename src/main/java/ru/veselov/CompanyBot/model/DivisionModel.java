package ru.veselov.CompanyBot.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"inquiries","managers"})
@AllArgsConstructor
@Builder
public class DivisionModel {
    private String divisionId;
    private String name;
    private Set<ManagerModel> managers = new HashSet<>();
   @Builder.Default private final Set<InquiryModel> inquiries=new HashSet<>();
}
