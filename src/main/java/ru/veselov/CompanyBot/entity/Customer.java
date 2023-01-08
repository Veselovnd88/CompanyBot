package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name="username")
    private String userName;
    //mappedBy - имя объекта, к которому привязан список
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    //HashSet - так как при объединении таблиц могут быть повторения строк с одним
    private Set<Inquiry> inquiryList = new HashSet<>();


}
