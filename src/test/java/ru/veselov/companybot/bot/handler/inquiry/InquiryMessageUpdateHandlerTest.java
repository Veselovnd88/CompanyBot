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
import org.springframework.test.util.ReflectionTestUtils;
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
import ru.veselov.companybot.bot.handler.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class InquiryMessageUpdateHandlerTest {

    private static final Integer MAX_MSG = 10;

    @Mock
    UserDataCacheFacade userDataCacheFacade;

    @Mock
    UserMessageChecker userMessageChecker;

    @InjectMocks
    InquiryMessageUpdateHandlerImpl inquiryMessageHandler;

    Update update;

    Message message;

    User user;

    InquiryModel inquiryModel;

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
        inquiryModel = Mockito.mock(InquiryModel.class);
        Mockito.when(userDataCacheFacade.getInquiry(user.getId())).thenReturn(inquiryModel);
        userDataCacheFacade.createInquiry(user.getId(), DivisionModel.builder().divisionId(UUID.randomUUID()).build());
        ReflectionTestUtils.setField(inquiryMessageHandler, "maxMessages", MAX_MSG, Integer.class);
    }

    @Test
    void shouldReturnSendMessageIfSentTooManyMessagesToInquiry() {
        List mockList = Mockito.mock(List.class);
        Mockito.when(mockList.size()).thenReturn(MAX_MSG + 1);//too many messages in inquiry
        Mockito.when(inquiryModel.getMessages()).thenReturn(mockList);

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).startsWith("Превышено");
    }

    @Test
    void shouldAddMessageWithText() {
        message.setEntities(null);
        message.setText("Test");

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage);
    }

    @Test
    void shouldAddMessageWithPhoto() {
        message.setEntities(null);
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileSize(100);
        message.setPhoto(List.of(photoSize));

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);
        checkAnsweredSendMessage(sendMessage);
    }

    @Test
    @SneakyThrows
    void shouldAddMessageWithAudio() {
        message.setEntities(null);
        Audio audio = new Audio();
        message.setAudio(audio);

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage);
    }

    @Test
    void shouldAddMessageWithDocument() {
        message.setEntities(null);
        Document document = new Document();
        message.setDocument(document);

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage);
    }

    @Test
    void shouldAddMessageWithVideo() {
        message.setEntities(null);
        Video video = new Video();
        message.setVideo(video);

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage);
    }

    @Test
    void shouldAddMessageWithAnimation() {
        message.setEntities(null);
        Animation animation = new Animation();
        message.setAnimation(animation);

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage);
    }

    public void checkAnsweredSendMessage(SendMessage sendMessage) {
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.AWAIT_CONTENT_MESSAGE),
                () -> Mockito.verify(userDataCacheFacade, Mockito.times(1)).getInquiry(user.getId()),
                () -> Mockito.verify(inquiryModel).addMessage(Mockito.any()),
                () -> Mockito.verify(userMessageChecker).checkForCustomEmojis(message),
                () -> Mockito.verify(userMessageChecker).checkForLongCaption(message)
        );
    }

}
