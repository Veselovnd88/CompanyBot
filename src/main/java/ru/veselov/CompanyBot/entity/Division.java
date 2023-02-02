package ru.veselov.CompanyBot.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "division")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Division.findDivision",
        query = "SELECT d FROM Division d "+
        "LEFT JOIN FETCH d.managers m "+
                "WHERE d.divisionId=:id")
})
public class Division {
    @Id
    @Column(name = "division_id")
    private String divisionId;
    @Column(columnDefinition = "varchar(950)")
    private String name;
    @ManyToMany(mappedBy = "divisions",cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH})
    private final Set<ManagerEntity> managers = new HashSet<>();

    @OneToMany(mappedBy = "division",
            cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE})
    private final Set<Inquiry> inquiries=new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return divisionId.equals(division.divisionId) && name.equals(division.name);
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
