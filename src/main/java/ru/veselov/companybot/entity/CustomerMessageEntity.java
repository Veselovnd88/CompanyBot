package ru.veselov.companybot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "message")
public class CustomerMessageEntity {

    @Id
    @Column(name = "message_id")
    private Integer messageId;

    //More convenient for save all message with all markups
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "message", columnDefinition = "jsonb")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "inquiry_id", referencedColumnName = "inquiry_id")
    private InquiryEntity inquiryEntity;

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
