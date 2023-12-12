package ru.veselov.companybot.bot.handler.message.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.handler.message.CommandUpdateHandler;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.util.TestUpdates;

@ExtendWith(MockitoExtension.class)
class MessageUpdateHandlerImplTest {

    @Mock
    UserDataCacheFacade userDataCache;

    @Mock
    CommandUpdateHandler commandUpdateHandler;

    @Mock
    BotStateHandlerContext botStateHandlerContext;

    @InjectMocks
    MessageUpdateHandlerImpl messageUpdateHandler;



    @Test
    void shouldCallCommandUpdateHandlerIfMessageHasCommandEntity() {
        Update updateWithMessageWithCommandByUser = TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);

        messageUpdateHandler.processUpdate(updateWithMessageWithCommandByUser);

        Mockito.verify(commandUpdateHandler).processUpdate(updateWithMessageWithCommandByUser);
    }

}