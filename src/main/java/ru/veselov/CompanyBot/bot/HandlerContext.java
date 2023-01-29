package ru.veselov.CompanyBot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.handler.managing.*;

import java.util.HashMap;

@Component
@Slf4j
public class HandlerContext {

    private final HashMap<BotState,UpdateHandler> messageHandlerContext = new HashMap<>();
    private final HashMap<BotState,UpdateHandler> callbackHandlerContext = new HashMap<>();

    public HandlerContext(ManagerMenuCallbackHandler managerMenuCallbackHandler,
                          ManageModeCallbackHandler manageModeCallbackHandler,
                          AddManagerFromForwardMessageHandler addManagerFromForwardMessageHandler,
                          AddDivisionToManagerFromCallbackHandler addDivisionToManagerFromCallbackHandler,
                          DivisionMenuCallbackHandler divisionMenuCallbackHandler,
                          AddDivisionTextMessageHandler addDivisionTextMessageHandler,
                          InformationAboutMessageHandler informationAboutMessageHandler) {
        callbackHandlerContext.put(BotState.MANAGE_MANAGER, managerMenuCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE, manageModeCallbackHandler);
        messageHandlerContext.put(BotState.AWAIT_MANAGER, addManagerFromForwardMessageHandler);
        messageHandlerContext.put(BotState.DELETE_MANAGER, addManagerFromForwardMessageHandler);
        callbackHandlerContext.put(BotState.ASSIGN_DIV, addDivisionToManagerFromCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE_DIVISION, divisionMenuCallbackHandler);
        messageHandlerContext.put(BotState.AWAIT_DIVISION, addDivisionTextMessageHandler);
        messageHandlerContext.put(BotState.MANAGE_ABOUT, informationAboutMessageHandler);

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
