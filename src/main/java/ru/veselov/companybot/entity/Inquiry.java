package ru.veselov.companybot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Integer inquiryId;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name="division_id",referencedColumnName = "division_id")
    private DivisionEntity divisionEntity;

    @OneToMany(mappedBy = "inquiry",orphanRemoval = true,cascade = CascadeType.ALL)
    private final Set<CustomerMessageEntity> messages = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customerEntity;

    public void addMessage(CustomerMessageEntity message){
        messages.add(message);
        message.setInquiry(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inquiry inquiry = (Inquiry) o;
        return date.equals(inquiry.date) && divisionEntity == inquiry.divisionEntity && messages.equals(inquiry.messages) && customerEntity.equals(inquiry.customerEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, divisionEntity, messages, customerEntity);
    }

}
