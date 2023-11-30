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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.impl.CommandUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.service.CustomerService;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private UserDataCache userDataCache;

    @Mock
    private ContactCache contactCache;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    CommandUpdateHandlerImpl commandHandler;

    Update update;
    User user;
    Message message;

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
    @SneakyThrows
    void shouldSaveNewUserChangeStateAndClearCache() {
        /*New user with no status */
        Mockito.when(userDataCache.getUserBotState(user.getId())).thenReturn(BotState.BEGIN);
        Mockito.when(message.getText()).thenReturn(BotCommands.START);
        SendMessage sendMessage = commandHandler.processUpdate(update);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(customerService).save(user),
                () -> Assertions.assertThat(sendMessage.getText()).isEqualTo(MessageUtils.GREETINGS),
                () -> Mockito.verify(userDataCache).setUserBotState(user.getId(), BotState.READY),
                () -> Mockito.verify(userDataCache).clear(user.getId()),
                () -> Mockito.verify(contactCache).clear(user.getId())
        );
    }

    @ParameterizedTest
    @EnumSource(value = BotState.class, names = {"BEGIN"}, mode = EnumSource.Mode.EXCLUDE)
    void startCommandWithStateTest(BotState botState) {
        /*Check cases of any states BEGIN*/
        Mockito.when(message.getText()).thenReturn(BotCommands.START);
        userDataCache.setUserBotState(user.getId(), botState);
        commandHandler.processUpdate(update);
        Mockito.verifyNoInteractions(customerService);
    }


    @Test
    @SneakyThrows
    void inquiryCommandWithStateTest() {
        /*Проверка входа в case с правильным статусом*/
        Mockito.when(message.getText()).thenReturn("/inquiry");
        userDataCache.setUserBotState(user.getId(), BotState.READY);
        BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
    }

    @Test
    @SneakyThrows
    void inquiryCommandWithWrongStateTest() {
        /*Проверка входа в case с неправильным статусом*/
        Mockito.when(message.getText()).thenReturn("/inquiry");
        for (var b : BotState.values()) {
            if (b != BotState.READY) {
                userDataCache.setUserBotState(user.getId(), b);
            }
        }
    }

    @Test
    @SneakyThrows
    void aboutCommandTest() {
        /*Проверка входа в case с любым статусом*/
        Mockito.when(message.getText()).thenReturn("/about");
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
        }
    }

    @Test
    @SneakyThrows
    void infoCommandTest() {
        /*Проверка входа в case с любым статусом*/
        Mockito.when(message.getText()).thenReturn("/info");
        for (var b : BotState.values()) {
            userDataCache.setUserBotState(user.getId(), b);
            BotApiMethod<?> botApiMethod = commandHandler.processUpdate(update);
        }
    }

    @Test
    void wrongCommandTest() {
        /*Проверка работы при подаче любой неправильной команды*/
        Mockito.when(message.getText()).thenReturn("/anyCommand");

    }
}