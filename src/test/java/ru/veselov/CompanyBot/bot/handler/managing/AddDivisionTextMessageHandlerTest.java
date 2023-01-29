package ru.veselov.CompanyBot.bot.handler.managing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.DivisionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AddDivisionTextMessageHandlerTest {

    @MockBean
    CompanyBot bot;

    @Autowired
    AddDivisionTextMessageHandler addDivisionTextMessageHandler;
    @Autowired
    UserDataCache userDataCache;
    @MockBean
    DivisionService divisionService;

    @MockBean
    CommandLineRunner commandLineRunner;

    Update update;
    Message message;
    User user;


    @BeforeEach
    void init(){
        update = spy(Update.class);
        message=spy(Message.class);
        user = spy(User.class);
        update.setMessage(message);
        user.setId(100L);
        message.setFrom(user);
        userDataCache.setUserBotState(user.getId(),BotState.AWAIT_DIVISION);
    }


    @ParameterizedTest
    @ValueSource(strings = {"lt",""," ","a adf", "aa asdfasdfasdfasdfasdfasdfasdfsdfasdfasdfasdfasdfasdf"})
    void noCorrectInputTest(String field){
        message.setText(field);
        assertInstanceOf(SendMessage.class,addDivisionTextMessageHandler.processUpdate(update));
        assertEquals(BotState.AWAIT_DIVISION,userDataCache.getUserBotState(user.getId()));
        verify(divisionService,never()).save(any(DivisionModel.class));
    }

    @ParameterizedTest
    @ValueSource(strings ={"lt asdf", "df df", "dd fff", "dd dd dd dd dd dd dd d"})
    void correctInputTest(String field){
        message.setText(field);
        assertInstanceOf(SendMessage.class, addDivisionTextMessageHandler.processUpdate(update));
        assertEquals(BotState.MANAGE,userDataCache.getUserBotState(user.getId()));
        verify(divisionService).save(any(DivisionModel.class));
    }

}