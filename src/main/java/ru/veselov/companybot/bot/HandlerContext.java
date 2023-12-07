package ru.veselov.companybot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.bot.context.UpdateHandler;
import ru.veselov.companybot.bot.handler.callback.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.handler.impl.ContactMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.impl.InquiryMessageUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.inquiry.DivisionCallbackHandler;

import java.util.EnumMap;
import java.util.Map;

@Component
@Slf4j
public class HandlerContext {

    private final Map<BotState, UpdateHandler> messageHandlerContext = new EnumMap<>(BotState.class);
    private final Map<BotState, UpdateHandler> callbackHandlerContext = new EnumMap<>(BotState.class);

    public HandlerContext(
            DivisionCallbackHandler divisionCallbackHandler,
            ContactCallbackUpdateHandler contactCallbackHandler,
            ContactMessageUpdateHandlerImpl contactMessageHandler,
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
