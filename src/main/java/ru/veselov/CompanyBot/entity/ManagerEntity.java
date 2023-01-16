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
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id")//FIXME One to Many but Bidirectional
    private Set<Division> divisions = new HashSet<>();

}
