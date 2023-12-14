package ru.veselov.companybot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
@NamedQuery(name = "CustomerEntity.findCustomerWithContacts",
        query = "SELECT c FROM CustomerEntity c " +
                "LEFT JOIN FETCH c.contacts cn " +
                "WHERE c.id=:id")

public class CustomerEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @OneToMany(mappedBy = "customerEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    @Column(name = "contact")
    private Set<ContactEntity> contacts = new HashSet<>();

    //mappedBy - to this object we bind list
    @OneToMany(mappedBy = "customerEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<InquiryEntity> inquiries = new HashSet<>();

}
