package ru.veselov.CompanyBot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.handler.*;

import java.util.HashMap;

@Component
@Slf4j
public class HandlerContext {

    private final HashMap<BotState,UpdateHandler> handlerContext = new HashMap<>();

    private final ManageManagerByAdminCallbackHandler manageManagerByAdminCallbackHandler;
    private final ManageCallbackHandler manageCallbackHandler;
    private final AddManagerByAdminMessageHandler addManagerByAdminMessageHandler;
    private final AddManagerByAdminCallbackHandler addManagerByAdminCallbackHandler;
    private final ManageDivisionCallbackHandler manageDivisionCallbackHandler;
    private final ManageDivisionMessageHandler manageDivisionMessageHandler;

    public HandlerContext(ManageManagerByAdminCallbackHandler manageManagerByAdminCallbackHandler, ManageCallbackHandler manageCallbackHandler, AddManagerByAdminMessageHandler addManagerByAdminMessageHandler, AddManagerByAdminCallbackHandler addManagerByAdminCallbackHandler, ManageDivisionCallbackHandler manageDivisionCallbackHandler, ManageDivisionMessageHandler manageDivisionMessageHandler) {
        this.manageManagerByAdminCallbackHandler = manageManagerByAdminCallbackHandler;
        this.manageCallbackHandler = manageCallbackHandler;
        this.addManagerByAdminMessageHandler = addManagerByAdminMessageHandler;
        this.addManagerByAdminCallbackHandler = addManagerByAdminCallbackHandler;
        this.manageDivisionCallbackHandler = manageDivisionCallbackHandler;
        this.manageDivisionMessageHandler = manageDivisionMessageHandler;
        handlerContext.put(BotState.MANAGE_MANAGER,manageManagerByAdminCallbackHandler);
        handlerContext.put(BotState.MANAGE, manageCallbackHandler);
        handlerContext.put(BotState.AWAIT_MANAGER,addManagerByAdminMessageHandler);
        handlerContext.put(BotState.DELETE_MANAGER,addManagerByAdminMessageHandler);
        handlerContext.put(BotState.ASSIGN_DIV, addManagerByAdminCallbackHandler);
        handlerContext.put(BotState.MANAGE_DIVISION, manageDivisionCallbackHandler);
        handlerContext.put(BotState.AWAIT_DIVISION, manageDivisionMessageHandler);

    }


    public boolean isInContext(BotState botState){
        return handlerContext.containsKey(botState);
    }

    public UpdateHandler getHandler(BotState botState){
        return handlerContext.get(botState);
    }
}
