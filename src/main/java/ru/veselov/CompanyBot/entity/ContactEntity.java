package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Contact;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contact")
@Getter
@Setter
@NoArgsConstructor
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
public class ContactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private int contactId;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;

    @Type(type = "jsonb")
    @Column(name = "card",columnDefinition = "jsonb")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactEntity that = (ContactEntity) o;
        return Objects.equals(phone, that.phone) && Objects.equals(email, that.email) && Objects.equals(contact, that.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, email, contact);
    }
}
