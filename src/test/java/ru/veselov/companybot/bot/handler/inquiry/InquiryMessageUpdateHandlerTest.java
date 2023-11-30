package ru.veselov.companybot.bot.handler.inquiry;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import ru.veselov.companybot.bot.handler.inquiry.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class InquiryMessageUpdateHandlerTest {

    @Mock
    UserDataCache userDataCache;

    @InjectMocks
    InquiryMessageUpdateHandlerImpl inquiryMessageHandler;

    Update update;

    Message message;

    User user;

    @BeforeEach
    void init() {
        update = spy(Update.class);
        message = spy(Message.class);
        user = spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        message.setEntities(List.of(messageEntity));
        InquiryModel inquiryModel = new InquiryModel();
        Mockito.when(userDataCache.getInquiry(user.getId())).thenReturn(DivisionModel.builder().divisionId(UUID.randomUUID()).build())
        userDataCache.createInquiry(user.getId(), DivisionModel.builder().divisionId(UUID.randomUUID()).build());
    }

    @Test
    void shouldThrowExceptionForTooLongMessage() {
        message.setCaption("i".repeat(1025));//create too long string
        Assertions.assertThatThrownBy(
                () -> inquiryMessageHandler.processUpdate(update)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @Test
    @SneakyThrows
    void manyMessagesTest() {
        for (int i = 0; i < 15; i++) {
            userDataCache.getInquiry(user.getId()).addMessage(new Message());
        }
        assertTrue(((SendMessage) inquiryMessageHandler.processUpdate(update)).getText()
                .startsWith("Превышено"));
    }

    @Test
    void customEmojiTest() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("custom_emoji");
        message.setEntities(List.of(messageEntity));
        assertThrows(NoAvailableActionSendMessageException.class,
                () -> inquiryMessageHandler.processUpdate(update));
    }

    @Test
    @SneakyThrows
    void messageWithText() {
        message.setEntities(null);
        message.setText("Test");
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    @SneakyThrows
    void messageWithPhoto() {
        message.setEntities(null);
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileSize(100);
        message.setPhoto(List.of(photoSize));
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    @SneakyThrows
    void messageWithAudio() {
        message.setEntities(null);
        Audio audio = new Audio();
        message.setAudio(audio);
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    @SneakyThrows
    void messageWithDocument() {
        message.setEntities(null);
        Document document = new Document();
        message.setDocument(document);
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    @SneakyThrows
    void messageWithVideo() {
        message.setEntities(null);
        Video video = new Video();
        message.setVideo(video);
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }

    @Test
    @SneakyThrows
    void messageWithAnimation() {
        message.setEntities(null);
        Animation animation = new Animation();
        message.setAnimation(animation);
        assertEquals(MessageUtils.AWAIT_CONTENT_MESSAGE,
                ((SendMessage) inquiryMessageHandler.processUpdate(update)).getText());
        assertEquals(1, userDataCache.getInquiry(user.getId()).getMessages().size());
    }
}