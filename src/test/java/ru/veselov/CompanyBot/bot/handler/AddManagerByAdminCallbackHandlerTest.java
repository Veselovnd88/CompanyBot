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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.handler.managing.AddManagerByAdminCallbackHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddManagerByAdminCallbackHandlerTest {

    @MockBean
    CompanyBot bot;

    @Value("${bot.adminId}")
    private Long adminId;

    @Autowired
    private AddManagerByAdminCallbackHandler addManagerByAdminCallbackHandler;
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
    HashMap<String, Division> divs = new HashMap<>();//FIXME not need

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
        adminCache.addManager(adminId,manager);
        callbackQuery.setMessage(message);
        when(divisionService.findAll()).thenReturn(
                List.of(Division.builder().divisionId("LT").name("LTTTTT").build(),
                        Division.builder().divisionId("T").name("TTTTTTTt").build())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"T1","T1+marked","T2","T2+marked"})
    void divisionButtonPressTest(String field) throws NoDivisionsException {
        //Checking enter to method with pressing of according button
        callbackQuery.setData(field);
        addManagerByAdminCallbackHandler.processUpdate(update);
    }
    @Test
    void saveButtonTest(){
        //Checking pressing save button
        callbackQuery.setData("save");
        addManagerByAdminCallbackHandler.processUpdate(update);
        verify(managerService).saveWithDivisions(manager,divisionKeyboardUtils.getMarkedDivisions(200L));
        assertEquals(BotState.READY,userDataCache.getUserBotState(adminId));
    }

    //todo test for empty list of divisions
}