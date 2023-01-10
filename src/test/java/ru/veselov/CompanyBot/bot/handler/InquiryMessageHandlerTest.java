package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.Department;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class InquiryMessageHandlerTest {
    @Autowired
    UserDataCache userDataCache;
    @Autowired
    InquiryMessageHandler inquiryMessageHandler;

    Update update;
    Message message;
    User user;

    @BeforeEach
    void init(){
        update=spy(Update.class);
        message=spy(Message.class);
        user =spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        message.setEntities(List.of(messageEntity));
    }

    @Test
    void longCaptionTest(){
        /*Проверка на длинное описание*/
        userDataCache.createInquiry(user.getId(), Department.COMMON);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<1025; i++){
            sb.append("i");
        }
        message.setCaption(sb.toString());
        assertEquals(MessageUtils.CAPTION_TOO_LONG,
                ((SendMessage)inquiryMessageHandler.processUpdate(update)).getText());
    }



}