package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "division")
@Getter
@Setter
@NoArgsConstructor
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "division_id")
    private Integer divisionId;

    @Column(unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return name.equals(division.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
