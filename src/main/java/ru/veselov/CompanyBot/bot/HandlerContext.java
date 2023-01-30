package ru.veselov.CompanyBot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.CompanyBot.bot.handler.ContactCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.ContactMessageHandler;
import ru.veselov.CompanyBot.bot.handler.DivisionCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.InquiryMessageHandler;
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
                          InformationAboutMessageHandler informationAboutMessageHandler,
                          DivisionCallbackHandler divisionCallbackHandler,
                          ContactCallbackHandler contactCallbackHandler,
                          ContactMessageHandler contactMessageHandler,
                          InquiryMessageHandler inquiryMessageHandler) {
        callbackHandlerContext.put(BotState.MANAGE_MANAGER, managerMenuCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE, manageModeCallbackHandler);
        callbackHandlerContext.put(BotState.ASSIGN_DIV, addDivisionToManagerFromCallbackHandler);
        callbackHandlerContext.put(BotState.MANAGE_DIVISION, divisionMenuCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_DIVISION_FOR_INQUIRY,divisionCallbackHandler);

        callbackHandlerContext.put(BotState.AWAIT_CONTACT,contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_EMAIL,contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_PHONE,contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_SHARED, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_NAME,contactCallbackHandler);

        messageHandlerContext.put(BotState.AWAIT_MANAGER, addManagerFromForwardMessageHandler);
        messageHandlerContext.put(BotState.DELETE_MANAGER, addManagerFromForwardMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_DIVISION, addDivisionTextMessageHandler);
        messageHandlerContext.put(BotState.MANAGE_ABOUT, informationAboutMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_MESSAGE, inquiryMessageHandler);

        messageHandlerContext.put(BotState.AWAIT_CONTACT,contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_EMAIL,contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_PHONE,contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_SHARED, contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_NAME,contactMessageHandler);



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
