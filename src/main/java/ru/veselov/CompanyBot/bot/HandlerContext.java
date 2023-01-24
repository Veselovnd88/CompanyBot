package ru.veselov.CompanyBot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.handler.*;

import java.util.HashMap;

@Component
@Slf4j
public class HandlerContext {

    private final HashMap<BotState,UpdateHandler> messageHandlerContext = new HashMap<>();
    private final HashMap<BotState,UpdateHandler> callbackHandlerContext = new HashMap<>();

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
        callbackHandlerContext.put(BotState.MANAGE_MANAGER,manageManagerByAdminCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE, manageCallbackHandler);
        messageHandlerContext.put(BotState.AWAIT_MANAGER,addManagerByAdminMessageHandler);
        messageHandlerContext.put(BotState.DELETE_MANAGER,addManagerByAdminMessageHandler);
        callbackHandlerContext.put(BotState.ASSIGN_DIV, addManagerByAdminCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE_DIVISION, manageDivisionCallbackHandler);
        messageHandlerContext.put(BotState.AWAIT_DIVISION, manageDivisionMessageHandler);

    }


    public boolean isInContext(BotState botState){
        return callbackHandlerContext.containsKey(botState)||messageHandlerContext.containsKey(botState);
    }
    public boolean isInMessageContext(BotState botState){
        return messageHandlerContext.containsKey(botState);
    }
    public boolean isInCallbackContext(BotState botState){
        return callbackHandlerContext.containsKey(botState);
    }

    public UpdateHandler getMessageHandler(BotState botState){
        return messageHandlerContext.get(botState);
    }
    public UpdateHandler getCallbackHandler(BotState botState){
        return callbackHandlerContext.get(botState);
    }
}
