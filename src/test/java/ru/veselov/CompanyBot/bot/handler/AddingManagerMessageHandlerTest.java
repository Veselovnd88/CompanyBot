package ru.veselov.CompanyBot.bot.handler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.managing.AddingManagerMessageHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddingManagerMessageHandlerTest {
    @MockBean
    CompanyBot bot;

    @Value("${bot.adminId}")
    private Long adminId;
    @MockBean
    private AdminCache adminCache;
    @Autowired
    private  UserDataCache userDataCache;
    @MockBean
    private  DivisionKeyboardUtils divisionKeyboardUtils;
    @Autowired
    private AddingManagerMessageHandler addingManagerMessageHandler;
    Update update;
    Message message;
    User user;
    User user2;
    @BeforeEach
    void init() throws NoDivisionsException {
        update=spy(Update.class);
        message=spy(Message.class);
        user =spy(User.class);
        user2=spy(User.class);
        update.setMessage(message);
        message.setFrom(user);
        user.setId(adminId);
        user2.setUserName("Vasya");
        user2.setId(100L);
        message.setForwardFrom(user2);
        when(divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),user2.getId())).thenReturn(new InlineKeyboardMarkup());
    }


    @Test
    void forwardedMessageTest() throws NoDivisionsException {
        //Test checks if status changes after transfering user's message
        addingManagerMessageHandler.processUpdate(update);
        assertEquals(BotState.ASSIGN_DIV,userDataCache.getUserBotState(adminId));
        verify(divisionKeyboardUtils).getAdminDivisionKeyboard(user.getId(),user2.getId());
        verify(adminCache).addManager(adminId,user2);
    }

    @SneakyThrows
    @Test
    void forwardedMessageNoForwardedTest(){
        //Checking if message wasnt forwarded from another user
        userDataCache.setUserBotState(adminId,BotState.AWAIT_MANAGER);
        //Test checks if status changes after transfering user's message
        message.setForwardFrom(null);
        addingManagerMessageHandler.processUpdate(update);
        assertEquals(BotState.AWAIT_MANAGER,userDataCache.getUserBotState(adminId));
        verify(divisionKeyboardUtils,never()).getAdminDivisionKeyboard(user.getId(),user2.getId());
        verify(adminCache,never()).addManager(adminId,user2);
    }

}