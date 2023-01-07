package ru.veselov.CompanyBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.veselov.CompanyBot.model.Department;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Integer inquiryId;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;


    @Column
    @Enumerated(EnumType.STRING)
    private Department department;

    @OneToMany(mappedBy = "inquiry",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<CustomerMessageEntity> messages = new LinkedList<>();

    //TODO привязать к Customer

}
