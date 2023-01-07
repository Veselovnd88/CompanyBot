package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
@Table(name = "message")
public class CustomerMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Сразу сохраняем в json со всеми разметками и ссылками, для более удобной пересылки в чат
    @Type(type = "jsonb")
    @Column(name = "message",columnDefinition = "jsonb")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "inquiry_id",referencedColumnName = "id")
    private Inquiry inquiry;



}
