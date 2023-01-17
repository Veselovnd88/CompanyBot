package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "manager")
@Getter
@Setter
@NoArgsConstructor
public class ManagerEntity {

    @Id
    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name="username")
    private String userName;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(
            name="manager_division",
            joinColumns = @JoinColumn(name="manager_id"),
            inverseJoinColumns = @JoinColumn(name = "division_id")
    )
    private Set<Division> divisions = new HashSet<>();
    //Bidirectional relations for ManyToMany
    public void addDivision(Division division){
        this.divisions.add(division);
        division.getManagers().add(this);
    }
    public void removeDivision(Division division){
        this.divisions.remove(division);
        division.getManagers().remove(this);
    }

}
