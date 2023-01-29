package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.ManagerModel;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddManagerFromForwardMessageHandlerTest {
    @MockBean
    CompanyBot bot;

    @Value("${bot.adminId}")
    private Long adminId;
    @MockBean
    private AdminCache adminCache;
    @MockBean
    private ManagerService managerService;
    @Autowired
    private  UserDataCache userDataCache;
    @MockBean
    private  DivisionKeyboardUtils divisionKeyboardUtils;
    @Autowired
    private AddManagerFromForwardMessageHandler addManagerFromForwardMessageHandler;
    Update update;
    Message message;
    User user;
    User user2;
    @SneakyThrows
    @BeforeEach
    void init(){
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

    @SneakyThrows
    @Test
    void forwardedMessageAddTest(){
        //Test checks if status changes after transferring user's message
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_MANAGER);
        addManagerFromForwardMessageHandler.processUpdate(update);
        assertEquals(BotState.ASSIGN_DIV,userDataCache.getUserBotState(adminId));
        verify(divisionKeyboardUtils).getAdminDivisionKeyboard(user.getId(),user2.getId());
        verify(adminCache).addManager(anyLong(),any(ManagerModel.class));
    }

    @SneakyThrows
    @Test
    void forwardedMessageNoForwardedTest(){
        //Checking if message wasn't forwarded from another user
        userDataCache.setUserBotState(adminId,BotState.AWAIT_MANAGER);
        //Test checks if status changes after transferring user's message
        message.setForwardFrom(null);
        addManagerFromForwardMessageHandler.processUpdate(update);
        assertEquals(BotState.AWAIT_MANAGER,userDataCache.getUserBotState(adminId));
        verify(divisionKeyboardUtils,never()).getAdminDivisionKeyboard(user.getId(),user2.getId());
        ManagerModel manager = ManagerModel.builder().managerId(user2.getId()).build();
        verify(adminCache,never()).addManager(adminId,manager);
    }
    @SneakyThrows
    @Test
    void forwardManagerDeleteTest(){
        userDataCache.setUserBotState(user.getId(),BotState.DELETE_MANAGER);
        assertInstanceOf(SendMessage.class, addManagerFromForwardMessageHandler.processUpdate(update));
        assertEquals(BotState.MANAGE,userDataCache.getUserBotState(adminId));
        ManagerModel manager = ManagerModel.builder().managerId(user2.getId()).build();
        verify(managerService).remove(manager);
    }

}