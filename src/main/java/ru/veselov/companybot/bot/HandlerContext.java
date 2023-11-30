package ru.veselov.companybot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.handler.inquiry.ContactCallbackHandler;
import ru.veselov.companybot.bot.handler.inquiry.ContactMessageHandler;
import ru.veselov.companybot.bot.handler.inquiry.DivisionCallbackHandler;
import ru.veselov.companybot.bot.handler.inquiry.impl.InquiryMessageUpdateHandlerImpl;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HandlerContext {

    private final Map<BotState, UpdateHandler> messageHandlerContext = new HashMap<>();
    private final Map<BotState, UpdateHandler> callbackHandlerContext = new HashMap<>();

    public HandlerContext(
            DivisionCallbackHandler divisionCallbackHandler,
            ContactCallbackHandler contactCallbackHandler,
            ContactMessageHandler contactMessageHandler,
            InquiryMessageUpdateHandlerImpl inquiryMessageHandler) {
        callbackHandlerContext.put(BotState.AWAIT_DIVISION_FOR_INQUIRY, divisionCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_MESSAGE, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_CONTACT, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_EMAIL, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_PHONE, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_SHARED, contactCallbackHandler);
        callbackHandlerContext.put(BotState.AWAIT_NAME, contactCallbackHandler);

        messageHandlerContext.put(BotState.AWAIT_MESSAGE, inquiryMessageHandler);

        messageHandlerContext.put(BotState.AWAIT_CONTACT, contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_EMAIL, contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_PHONE, contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_SHARED, contactMessageHandler);
        messageHandlerContext.put(BotState.AWAIT_NAME, contactMessageHandler);
    }


    public boolean isInMessageContext(BotState botState) {
        return messageHandlerContext.containsKey(botState);
    }

    public boolean isInCallbackContext(BotState botState) {
        return callbackHandlerContext.containsKey(botState);
    }

    public UpdateHandler getMessageHandler(BotState botState) {
        return messageHandlerContext.get(botState);
    }

    public UpdateHandler getCallbackHandler(BotState botState) {
        return callbackHandlerContext.get(botState);
    }
}
