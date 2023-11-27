package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.SneakyThrows;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.DivisionService;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddDivisionToManagerFromCallbackHandlerTest {

    @MockBean
    CompanyBot bot;

    @Value("${bot.adminId}")
    private Long adminId;

    @Autowired
    private AddDivisionToManagerFromCallbackHandler addDivisionToManagerFromCallbackHandler;
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
    ManagerModel manager;
    Message message;

    @BeforeEach
    void init() {
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
        manager = spy(ManagerModel.class);
        manager.setManagerId(200L);
        manager.setUserName("VVasya");
        adminCache.addManager(user.getId(),manager);
        callbackQuery.setMessage(message);
        when(divisionService.findAll()).thenReturn(
                List.of(DivisionModel.builder().divisionId("LT").name("LTTTTT").build(),
                        DivisionModel.builder().divisionId("T").name("TTTTTTTt").build())
        );

    }

    @ParameterizedTest
    @ValueSource(strings = {"LT","LT+marked","T","T+marked"})
    @SneakyThrows
    void divisionButtonPressTestWithNotMarkedButtons(String field) throws NoDivisionsException {
        //Checking enter to method with pressing of according button
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),manager.getManagerId());
        callbackQuery.setData(field);
        BotApiMethod<?> botApiMethod = addDivisionToManagerFromCallbackHandler.processUpdate(update);
        assertInstanceOf(EditMessageReplyMarkup.class, botApiMethod);
    }
    @Test
    @SneakyThrows
    void saveButtonTest() throws NoDivisionsException {
        //Checking pressing save button
        divisionKeyboardUtils.getAdminDivisionKeyboard(user.getId(),manager.getManagerId());
        callbackQuery.setData("LT+marked");
        addDivisionToManagerFromCallbackHandler.processUpdate(update);
        callbackQuery.setData("save");
        Set<DivisionModel> divisions = divisionKeyboardUtils.getMarkedDivisions(user.getId());
        manager.setDivisions(divisions);
        addDivisionToManagerFromCallbackHandler.processUpdate(update);
        verify(managerService).save(manager);
        assertNull(adminCache.getManager(user.getId()));
        assertThrows(NoDivisionsException.class,()-> divisionKeyboardUtils.getCachedDivisions());
        assertEquals(BotState.READY,userDataCache.getUserBotState(adminId));
    }

    @Test
    void noDivisionsInDbTest() {
        when(divisionService.findAll()).thenReturn(Collections.emptyList());
        callbackQuery.setData("LT");
        assertThrows(NoAvailableActionSendMessageException.class, ()->
                addDivisionToManagerFromCallbackHandler.processUpdate(update));
    }

}