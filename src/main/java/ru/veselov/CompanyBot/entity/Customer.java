package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
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

    @Type(type = "jsonb")
    @Column(name = "contact",columnDefinition = "jsonb")
    private Message contact;

    //mappedBy - имя объекта, к которому привязан список
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    //HashSet - так как при объединении таблиц могут быть повторения строк с одним
    private Set<Inquiry> inquiryList = new HashSet<>();


}
