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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.service.InquiryService;
import ru.veselov.CompanyBot.service.ManagerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;

import java.util.HashMap;
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
    @MockBean
    private DivisionKeyboardUtils divisionKeyboardUtils;

    @Autowired
    private UserDataCache userDataCache;
    @MockBean
    private ManagerService managerService;
    @Autowired
    private AdminCache adminCache;
    Update update;
    CallbackQuery callbackQuery;
    User user;
    HashMap<String, Division> divs = new HashMap<>();

    @BeforeEach
    void init(){
        update=spy(Update.class);
        callbackQuery=spy(CallbackQuery.class);
        update.setCallbackQuery(callbackQuery);
        user=spy(User.class);
        user.setId(adminId);
        callbackQuery.setFrom(user);
        callbackQuery.setId("100");

        divs.put("T1",null);
        divs.put("T1+marked",null);
        divs.put("T2",null);
        divs.put("T2+marked",null);
        when(divisionKeyboardUtils.getKeyboardDivs()).thenReturn(divs);

    }

    @ParameterizedTest
    @ValueSource(strings = {"T1","T1+marked","T2","T2+marked"})
    void divisionButtonPressTest(String field){
        callbackQuery.setData(field);
        addManagerByAdminCallbackHandler.processUpdate(update);
        verify(divisionKeyboardUtils).divisionChooseField(any(Update.class),anyString());
    }
    @Test
    void saveButtonTest(){
        callbackQuery.setData("save");
        User manager = new User();
        manager.setId(200L);
        when(divisionKeyboardUtils.getMarkedDivisions(anyLong())).thenReturn(Set.of(new Division()));
        manager.setUserName("VVasya");
        adminCache.addManager(adminId,manager);
        addManagerByAdminCallbackHandler.processUpdate(update);
        verify(managerService).saveWithDivisions(manager,divisionKeyboardUtils.getMarkedDivisions(200L));
        assertEquals(BotState.READY,userDataCache.getUserBotState(adminId));
    }
}