package ru.veselov.CompanyBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@TypeDef(name="jsonb",typeClass = JsonBinaryType.class)
@Table(name = "message")
public class CustomerMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    //Сразу сохраняем в json со всеми разметками и ссылками, для более удобной пересылки в чат
    @Type(type = "jsonb")
    @Column(name = "message",columnDefinition = "jsonb")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "inquiry_id",referencedColumnName = "inquiry_id")
    private Inquiry inquiry;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerMessageEntity that = (CustomerMessageEntity) o;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
