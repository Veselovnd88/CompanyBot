package ru.veselov.companybot.bot.handler.message.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateMessageHandlerContext;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.MessageProcessingException;
import ru.veselov.companybot.service.CompanyInfoService;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class AboutInfoUpdateHandlerImplTest {

    @Mock
    BotStateMessageHandlerContext context;

    @Mock
    CompanyInfoService companyInfoService;

    @Mock
    UserMessageChecker userMessageChecker;

    @Mock
    UserDataCacheFacade userDataCacheFacade;

    @InjectMocks
    AboutInfoUpdateHandlerImpl aboutInfoUpdateHandler;

    @Test
    void processUpdate_ifMessageIsCorrect_ReturnSendMessageWithGoodAnswer() {
        Update update = TestUpdates.getUpdateWithMessageWithTextContentByAdmin();
        Message message = update.getMessage();
        SendMessage sendMessage = aboutInfoUpdateHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).as("Check if sendMessage has needed text")
                .isEqualTo(MessageUtils.NEW_INFO_MSG);
        Assertions.assertThat(MessageUtils.getAbout().getText())
                .as("Check if ABOUT field was set").isEqualTo(message.getText());
        Mockito.verify(userMessageChecker).checkForCustomEmojis(message);
        Mockito.verify(companyInfoService).save(message);
        Mockito.verify(userDataCacheFacade).setUserBotState(TestUtils.ADMIN_ID, BotState.READY);
    }

    @ParameterizedTest
    @MethodSource("getBadText")
    void processUpdate_ifMessageIsBad_ThrowMessageProcessingException() {
        Update update = TestUpdates.getUpdateWithMessageWithTextContentByAdmin();
        Message message = update.getMessage();
        message.setText("a".repeat(905));

        Assertions.assertThatExceptionOfType(MessageProcessingException.class)
                .as("Check if MessageProcessingException will be thrown")
                .isThrownBy(() -> aboutInfoUpdateHandler.processUpdate(update));
        Mockito.verifyNoInteractions(userMessageChecker, companyInfoService, userDataCacheFacade);
    }

    @Test
    void registerInContext_addStateToContext() {
        aboutInfoUpdateHandler.registerInContext();

        Mockito.verify(context).addToBotStateContext(BotState.AWAIT_INFO, aboutInfoUpdateHandler);
    }

    @Test
    void getAvailableStated_ReturnAwaitInfoStateOnly() {
        Set<BotState> availableStates = aboutInfoUpdateHandler.getAvailableStates();

        Assertions.assertThat(availableStates).as("Check if available states is that we want")
                .isEqualTo(Set.of(BotState.AWAIT_INFO));
    }

    private static Stream<String> getBadText() {
        return Stream.of(
                "",
                "a".repeat(901)
        );
    }

}
