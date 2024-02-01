package ru.veselov.companybot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "inquiry")
public class InquiryEntity {

    @Id
    @GeneratedValue
    @Column(name = "inquiry_id")
    private UUID inquiryId;

    @Column
    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "division_id", referencedColumnName = "division_id")
    private DivisionEntity division;

    @OneToMany(mappedBy = "inquiryEntity", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private final Set<CustomerMessageEntity> messages = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;

    public void addMessage(CustomerMessageEntity message) {
        messages.add(message);
        message.setInquiryEntity(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InquiryEntity inquiryEntity = (InquiryEntity) o;
        return date.equals(inquiryEntity.date) && division == inquiryEntity.division
                && messages.equals(inquiryEntity.messages) && customer.equals(inquiryEntity.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, division, messages, customer);
    }

}
