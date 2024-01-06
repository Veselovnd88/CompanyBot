package ru.veselov.companybot.bot.handler.message;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.message.impl.CommandUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUpdates;
import ru.veselov.companybot.util.TestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommandUpdateHandlerTest {

    @Mock
    private UserDataCacheFacade userDataCacheFacade;

    @Mock
    private CustomerService customerService;

    @Mock
    private DivisionKeyboardHelper divisionKeyboardHelper;

    @InjectMocks
    CommandUpdateHandlerImpl commandHandler;

    User user;

    Long userId;

    @BeforeEach
    void init() {
        user = TestUtils.getSimpleUser();
        userId = user.getId();
        ReflectionTestUtils.setField(commandHandler, "adminId", TestUtils.ADMIN_ID, Long.class);
    }

    @Test
    void shouldSaveNewUserChangeStateAndClearCacheForStartCommand() {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.START);
        /*New user with no status */
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.BEGIN);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(customerService).save(user),
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.GREETINGS),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(userId, BotState.READY),
                () -> Mockito.verify(userDataCacheFacade).clear(userId)
        );
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"BEGIN"}, mode = EnumSource.Mode.EXCLUDE)
    void shouldNotSaveCustomerIfStateIsBeginForStartCommand(BotState botState) {
        /*Check cases of any states except BEGIN*/
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.START);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(botState);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verifyNoInteractions(customerService),
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.GREETINGS),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(userId, BotState.READY),
                () -> Mockito.verify(userDataCacheFacade).clear(userId)
        );
    }


    @Test
    @SneakyThrows
    void shouldSetStateAndGiveKeyboardForInquiryCommand() {
        /*Check flow case with READY state */
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.INQUIRY);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.READY);
        Mockito.when(divisionKeyboardHelper.getCustomerDivisionKeyboard()).thenReturn(new InlineKeyboardMarkup());

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.CHOOSE_DEP),
                () -> Assertions.assertThat(sendMessage.getReplyMarkup()).isNotNull(),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(userId, BotState.AWAIT_DIVISION_FOR_INQUIRY)
        );

    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"READY"}, mode = EnumSource.Mode.EXCLUDE)
    void shouldThrowExceptionIfWrongStateForInquiryCommand(BotState botState) {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.INQUIRY);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(botState);
        Assertions.assertThatThrownBy(() -> commandHandler.processUpdate(update))
                .isInstanceOf(WrongBotStateException.class);
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class)
    void shouldGiveAboutInformationForAllOfStatesForAboutCommand(BotState botState) {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.ABOUT);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(botState);
        Message aboutMessage = new Message();
        aboutMessage.setText("text");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setLength(1);
        messageEntity.setType("bold");
        messageEntity.setOffset(2);
        aboutMessage.setEntities(List.of(messageEntity));
        MessageUtils.setAbout(aboutMessage);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).isEqualTo(aboutMessage.getText());
        Assertions.assertThat(sendMessage.getEntities()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class)
    void shouldGiveInfoTextForAllOfStatesForInfoCommand(BotState botState) {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.INFO);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(botState);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.INFO);
    }

    @Test
    @SneakyThrows
    void shouldSetStateAndInviteToSendCompanyInfoMessage() {
        /*Check flow case with READY state */
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.UPDATE_INFO);
        update.getMessage().setFrom(TestUtils.getAdminUser());
        User adminUser = TestUtils.getAdminUser();
        Mockito.when(userDataCacheFacade.getUserBotState(adminUser.getId())).thenReturn(BotState.READY);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText())
                        .as("Check if message is correct").isEqualTo(MessageUtils.AWAIT_INFO_MESSAGE),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(adminUser.getId(), BotState.AWAIT_INFO)
        );
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"READY"}, mode = EnumSource.Mode.EXCLUDE)
    @SneakyThrows
    void shouldThrowExceptionIfNotCorrectStateForUpdateInfoMessage(BotState wrongState) {
        /*Check flow case with READY state */
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.UPDATE_INFO);
        update.getMessage().setFrom(TestUtils.getAdminUser());
        User adminUser = TestUtils.getAdminUser();
        Mockito.when(userDataCacheFacade.getUserBotState(adminUser.getId())).thenReturn(wrongState);

        Assertions.assertThatExceptionOfType(WrongBotStateException.class)
                .as("Check if exception was thrown for wrong state")
                .isThrownBy(() -> commandHandler.processUpdate(update));
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionIfNoAdminTryToUpdateInfoMessage() {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.UPDATE_INFO);
        Mockito.when(userDataCacheFacade.getUserBotState(userId)).thenReturn(BotState.READY);

        Assertions.assertThatExceptionOfType(WrongBotStateException.class)
                .as("Check if exception was thrown for wrong state")
                .isThrownBy(() -> commandHandler.processUpdate(update));
    }

    @Test
    void shouldThrowExceptionForNotSupportedCommand() {
        Update update = TestUpdates.getUpdateWithMessageWithCommandByUser("/anyCommand");
        Assertions.assertThatThrownBy(() -> commandHandler.processUpdate(update))
                .isInstanceOf(WrongBotStateException.class);
    }

}
