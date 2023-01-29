package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "company_info")
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
public class CompanyInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Type(type = "jsonb")
    @Column(name = "info",columnDefinition = "jsonb")
    private Message info;

    @Temporal(TemporalType.TIMESTAMP)
    Date changedAt;
}
