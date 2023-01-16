package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "division")
@Getter
@Setter
@NoArgsConstructor
public class Division {

    @Id
    @Column(name = "division_id")
    private String divisionId;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "divisions",cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH})
    private Set<ManagerEntity> managers = new HashSet<>();

    public void addManagers(ManagerEntity manager){
        this.managers.add(manager);
        manager.getDivisions().add(this);
    }
    public void removeManager(ManagerEntity manager){
        this.managers.remove(manager);
        manager.getDivisions().remove(this);
    }

}
