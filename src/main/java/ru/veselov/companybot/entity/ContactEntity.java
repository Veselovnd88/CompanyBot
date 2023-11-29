package ru.veselov.companybot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contact")
@Getter
@Setter
@NoArgsConstructor
public class ContactEntity {

    @Id
    @GeneratedValue
    @Column(name = "contact_id")
    private UUID contactId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "phone")
    private String phone;

    @Email
    @Column(name = "email")
    private String email;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "card", columnDefinition = "jsonb")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customerEntity;

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
