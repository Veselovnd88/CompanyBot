package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ActiveProfiles("test")
class InquiryMessageHandlerTest {
    @MockBean
    CompanyBot companyBot;
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
        userDataCache.createInquiry(user.getId(), DivisionModel.builder().divisionId("L").build());
    }

    @Test
    void longCaptionTest(){
        /*Проверка на длинное описание*/
        message.setCaption("i".repeat(1025));//метод стринги
        assertEquals(MessageUtils.CAPTION_TOO_LONG,
                ((SendMessage)inquiryMessageHandler.processUpdate(update)).getText());
    }
    @Test
    void manyMessagesTest(){
        for(int i=0; i<15;i++){
            userDataCache.getInquiry(user.getId()).addMessage(new Message());
        }
        assertTrue(((SendMessage) inquiryMessageHandler.processUpdate(update)).getText()
                .startsWith("Превышено"));
    }

    @Test
    void customEmojiTest(){
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("custom_emoji");
        message.setEntities(List.of(messageEntity));
        assertEquals(MessageUtils.NO_CUSTOM_EMOJI,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
    }

    @Test
    void messageWithText(){
        message.setEntities(null);
        message.setText("Test");
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1,userDataCache.getInquiry(user.getId()).getMessages().size());
    }
    @Test
    void messageWithPhoto(){
        message.setEntities(null);
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileSize(100);
        message.setPhoto(List.of(photoSize));
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1,userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    void messageWithAudio(){
        message.setEntities(null);
        Audio audio = new Audio();
        message.setAudio(audio);
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1,userDataCache.getInquiry(user.getId()).getMessages().size());
    }
}