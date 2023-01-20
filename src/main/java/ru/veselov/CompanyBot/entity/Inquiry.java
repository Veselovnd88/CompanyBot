package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private Division division;

    @OneToMany(mappedBy = "inquiry",orphanRemoval = true,cascade = CascadeType.ALL)
    private final Set<CustomerMessageEntity> messages = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    public void addMessage(CustomerMessageEntity message){
        messages.add(message);
        message.setInquiry(this);
    }
    //Так как объект помещается в сет - переопределили
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inquiry inquiry = (Inquiry) o;
        return date.equals(inquiry.date) && division == inquiry.division && messages.equals(inquiry.messages) && customer.equals(inquiry.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, division, messages, customer);
    }
}
