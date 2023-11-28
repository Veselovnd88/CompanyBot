package ru.veselov.companybot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "manager")
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "ManagerEntity.findOneWithDivisions",
                query = "SELECT m FROM ManagerEntity m " +
                        "LEFT JOIN FETCH m.divisionEntities d " +
                        "WHERE m.managerId=:id")
})
public class ManagerEntity {

    @Id
    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "manager_division",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "division_id")
    )
    private Set<DivisionEntity> divisionEntities = new HashSet<>();

    //Bidirectional relations for ManyToMany
    public void addDivision(DivisionEntity divisionEntity) {
        this.divisionEntities.add(divisionEntity);
        divisionEntity.getManagers().add(this);
    }

    public void removeDivision(DivisionEntity divisionEntity) {
        this.divisionEntities.remove(divisionEntity);
        divisionEntity.getManagers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerEntity that = (ManagerEntity) o;
        return managerId.equals(that.managerId) && Objects.equals(firstName, that.firstName) && lastName.equals(that.lastName) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerId, firstName, lastName, userName);
    }

}
