package ru.veselov.companybot.bot.handler;

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

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.impl.CommandUpdateHandlerImpl;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.CustomerService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommandUpdateHandlerTest {

    @Mock
    private UserDataCacheFacade userDataCacheFacade;

    @Mock
    private ContactCache contactCache;

    @Mock
    private CustomerService customerService;

    @Mock
    private DivisionKeyboardHelper divisionKeyboardHelper;

    @InjectMocks
    CommandUpdateHandlerImpl commandHandler;

    Update update;

    User user;

    Message message;

/*    @BeforeAll
    static void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
    }*/

    @BeforeEach
    void init() {
        update = Mockito.spy(Update.class);
        message = Mockito.spy(Message.class);
        user = Mockito.spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(100L);
    }

    @Test
    void shouldSaveNewUserChangeStateAndClearCache() {
        /*New user with no status */
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(BotState.BEGIN);
        Mockito.when(message.getText()).thenReturn(BotCommands.START);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(customerService).save(user),
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.GREETINGS),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(user.getId(), BotState.READY),
                () -> Mockito.verify(userDataCacheFacade).clear(user.getId()),
                () -> Mockito.verify(contactCache).clear(user.getId())
        );
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"BEGIN"}, mode = EnumSource.Mode.EXCLUDE)
    void shouldNotSaveCustomerIfStateIsBEGIN(BotState botState) {
        /*Check cases of any states except BEGIN*/
        Mockito.when(message.getText()).thenReturn(BotCommands.START);
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(botState);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verifyNoInteractions(customerService),
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.GREETINGS),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(user.getId(), BotState.READY),
                () -> Mockito.verify(userDataCacheFacade).clear(user.getId()),
                () -> Mockito.verify(contactCache).clear(user.getId())
        );
    }


    @Test
    @SneakyThrows
    void shouldSetStateAndGiveKeyboard() {
        /*Check flow case with READY state */
        Mockito.when(message.getText()).thenReturn(BotCommands.INQUIRY);
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(BotState.READY);
        Mockito.when(divisionKeyboardHelper.getCustomerDivisionKeyboard()).thenReturn(new InlineKeyboardMarkup());

        SendMessage sendMessage = commandHandler.processUpdate(update);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.CHOOSE_DEP),
                () -> Assertions.assertThat(sendMessage.getReplyMarkup()).isNotNull(),
                () -> Mockito.verify(userDataCacheFacade).setUserBotState(user.getId(), BotState.AWAIT_DIVISION_FOR_INQUIRY)
        );

    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"READY"}, mode = EnumSource.Mode.EXCLUDE)
    void shouldThrowExceptionIfWrongStateForInquiryCommand(BotState botState) {
        Mockito.when(message.getText()).thenReturn(BotCommands.INQUIRY);
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(botState);
        Assertions.assertThatThrownBy(
                () -> commandHandler.processUpdate(update)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class)
    void shouldGiveAboutInformationForAllOfStates(BotState botState) {
        Mockito.when(message.getText()).thenReturn(BotCommands.ABOUT);
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(botState);
        Message aboutMessage = new Message();
        aboutMessage.setText("text");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setLength(1);
        messageEntity.setType("bold");
        messageEntity.setOffset(2);
        aboutMessage.setEntities(List.of(messageEntity));
        MessageUtils.setABOUT(aboutMessage);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).isEqualTo(aboutMessage.getText());
        Assertions.assertThat(sendMessage.getEntities()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class)
    void shouldGiveInfoTextForAllOfStates(BotState botState) {
        Mockito.when(message.getText()).thenReturn(BotCommands.INFO);
        Mockito.when(userDataCacheFacade.getUserBotState(user.getId())).thenReturn(botState);

        SendMessage sendMessage = commandHandler.processUpdate(update);

        Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.INFO);
    }

    @Test
    void shouldThrowExceptionForNotSupportedCommand() {
        Mockito.when(message.getText()).thenReturn("/anyCommand");
        Assertions.assertThatThrownBy(
                () -> commandHandler.processUpdate(update)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

}