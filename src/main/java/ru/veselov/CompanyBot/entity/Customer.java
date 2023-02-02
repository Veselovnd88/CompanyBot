package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
@NamedQueries({
        @NamedQuery(name = "Customer.findCustomerWithContacts",
                query = "SELECT c FROM Customer c "+
                        "LEFT JOIN FETCH c.contacts cn "+
                        "WHERE c.id=:id")
})
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
    @OneToMany(mappedBy = "customer",orphanRemoval = true,cascade = CascadeType.ALL)
    @Column(name = "contact")
    private Set<ContactEntity> contacts=new HashSet<>();

    //mappedBy - имя объекта, к которому привязан список
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    //HashSet - так как при объединении таблиц могут быть повторения строк с одним
    private Set<Inquiry> inquiryList = new HashSet<>();
}
