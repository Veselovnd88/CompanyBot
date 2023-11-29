package ru.veselov.companybot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "division")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DivisionEntity {

    @Id
    @GeneratedValue
    @Column(name = "division_id")
    private UUID divisionId;

    @Column(columnDefinition = "varchar(950)")
    private String name;

    @OneToMany(mappedBy = "division",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private final Set<InquiryEntity> inquiries = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DivisionEntity divisionEntity = (DivisionEntity) o;
        return divisionId.equals(divisionEntity.divisionId) && name.equals(divisionEntity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(divisionId, name);
    }

    @Override
    public String toString() {
        return "Division{" +
                "divisionId='" + divisionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
