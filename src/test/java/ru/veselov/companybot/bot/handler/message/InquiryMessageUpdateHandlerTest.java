package ru.veselov.companybot.bot.handler.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.handler.message.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class InquiryMessageUpdateHandlerTest {

    private static final Integer MAX_MSG = 10;

    @Mock
    UserDataCacheFacade userDataCacheFacade;

    @Mock
    UserMessageChecker userMessageChecker;

    @Mock
    BotStateHandlerContext context;

    @InjectMocks
    InquiryMessageUpdateHandlerImpl inquiryMessageHandler;

    Long userId;

    InquiryModel inquiryModel;

    @BeforeEach
    void init() {
        userId = TestUtils.getSimpleUser().getId();
        ReflectionTestUtils.setField(inquiryMessageHandler, "maxMessages", MAX_MSG, Integer.class);
    }

    @Test
    void shouldReturnSendMessageIfSentTooManyMessagesToInquiry() {
        inquiryModel = Mockito.mock(InquiryModel.class);
        Mockito.when(userDataCacheFacade.getInquiry(userId)).thenReturn(inquiryModel);
        userDataCacheFacade.createInquiry(userId, DivisionModel.builder().divisionId(UUID.randomUUID()).build());
        List mockList = Mockito.mock(List.class);
        Mockito.when(mockList.size()).thenReturn(MAX_MSG + 1);//too many messages in inquiry
        Mockito.when(inquiryModel.getMessages()).thenReturn(mockList);
        Update update = TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithContentByUser();

        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).startsWith("Превышено");
    }

    @ParameterizedTest
    @MethodSource("getUpdatesWithDifferentContentWithText")
    void shouldAddMessageToInquiryWithDifferentContent(Update update, Integer messagesQnt) {
        inquiryModel = Mockito.mock(InquiryModel.class);
        Mockito.when(userDataCacheFacade.getInquiry(userId)).thenReturn(inquiryModel);
        userDataCacheFacade.createInquiry(userId, DivisionModel.builder().divisionId(UUID.randomUUID()).build());
        Message message = update.getMessage();
        SendMessage sendMessage = inquiryMessageHandler.processUpdate(update);

        checkAnsweredSendMessage(sendMessage, message, messagesQnt);
    }

    @Test
    void shouldRegisterInContext() {
        inquiryMessageHandler.registerInContext();

        Mockito.verify(context).add(BotState.AWAIT_MESSAGE, inquiryMessageHandler);
    }

    @Test
    void shouldReturnAvailableStates() {
        Set<BotState> availableStates = inquiryMessageHandler.getAvailableStates();

        Assertions.assertThat(availableStates).isEqualTo(Set.of(BotState.AWAIT_MESSAGE));
    }

    public void checkAnsweredSendMessage(SendMessage sendMessage, Message message, Integer messagesQnt) {
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.AWAIT_CONTENT_MESSAGE),
                () -> Mockito.verify(userDataCacheFacade, Mockito.times(1)).getInquiry(userId),
                () -> Mockito.verify(inquiryModel, Mockito.times(messagesQnt)).addMessage(Mockito.any()),
                () -> Mockito.verify(userMessageChecker).checkForCustomEmojis(message),
                () -> Mockito.verify(userMessageChecker).checkForLongCaption(message)
        );
    }


    private static Stream<Arguments> getUpdatesWithDifferentContentWithText() {
        return Stream.of(
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithContentByUser(), 1),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithPhotoByUser(), 2),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithAudioByUser(), 2),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithAudioByUser(), 2),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithDocumentByUser(), 2),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithDocumentByUser(), 2),
                Arguments.of(TestUpdates.getUpdateWithMessageNoCommandNoEntitiesWithAnimationByUser(), 2)
        );
    }

}
