package ru.veselov.CompanyBot.bot.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.managing.AddingDivisionFromKeyboardCallbackHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddingDivisionFromKeyboardCallbackHandlerTest {

    @MockBean
    CompanyBot bot;

    @Value("${bot.adminId}")
    private Long adminId;

    @Autowired
    private AddingDivisionFromKeyboardCallbackHandler addingDivisionFromKeyboardCallbackHandler;
    @Autowired
    private DivisionKeyboardUtils divisionKeyboardUtils;
    @Autowired
    private UserDataCache userDataCache;
    @MockBean
    private ManagerService managerService;
    @MockBean
    DivisionService divisionService;
    @Autowired
    private AdminCache adminCache;
    Update update;
    CallbackQuery callbackQuery;
    User user;
    User manager;
    Message message;

    @BeforeEach
    void init() throws NoDivisionsException {
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(adminId);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");
        message=spy(Message.class);
        Chat chat = new Chat();
        chat.setId(adminId);
        message.setChat(chat);
        manager = spy(User.class);
        manager.setId(200L);
        manager.setUserName("VVasya");
        adminCache.addManager(user.getId(),manager);
        callbackQuery.setMessage(message);

        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),manager.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"LT","LT+marked","T","T+marked"})
    void divisionButtonPressTestWithNotMarkedButtons(String field) throws NoDivisionsException {
        //Checking enter to method with pressing of according button
        when(divisionService.findAll()).thenReturn(
                List.of(Division.builder().divisionId("LT").name("LTTTTT").build(),
                        Division.builder().divisionId("T").name("TTTTTTTt").build())
        );
        callbackQuery.setData(field);
        BotApiMethod<?> botApiMethod = addingDivisionFromKeyboardCallbackHandler.processUpdate(update);
        assertInstanceOf(EditMessageReplyMarkup.class, botApiMethod);
    }
    @Test
    void saveButtonTest(){
        //Checking pressing save button
        when(divisionService.findAll()).thenReturn(
                List.of(Division.builder().divisionId("LT").name("LTTTTT").build(),
                        Division.builder().divisionId("T").name("TTTTTTTt").build())
        );
        callbackQuery.setData("LT+marked");
        addingDivisionFromKeyboardCallbackHandler.processUpdate(update);
        callbackQuery.setData("save");
        Set<Division> divisions = divisionKeyboardUtils.getMarkedDivisions(user.getId());
        addingDivisionFromKeyboardCallbackHandler.processUpdate(update);
        verify(managerService).saveWithDivisions(manager,divisions);
        assertNull(adminCache.getManager(user.getId()));
        assertEquals(0,divisionKeyboardUtils.getCachedDivisions().size());
        assertEquals(BotState.READY,userDataCache.getUserBotState(adminId));
    }

    @Test
    void noDivisionsInDbTest(){//FIXME не отрабатывает exception
        when(divisionService.findAll()).thenReturn(Collections.emptyList());
        callbackQuery.setData("LT");
        assertInstanceOf(SendMessage.class,addingDivisionFromKeyboardCallbackHandler.processUpdate(update));
        /*assertThrows(NoDivisionsException.class, ()->{
            addingDivisionFromKeyboardCallbackHandler.processUpdate(update);
        }); FIXME перенести в проверку клавиатуры*/
    }

    //todo test for empty list of divisions
}