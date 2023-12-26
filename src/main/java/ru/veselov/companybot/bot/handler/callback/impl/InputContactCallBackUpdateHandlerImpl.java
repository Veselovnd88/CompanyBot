package ru.veselov.companybot.bot.handler.callback.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.handler.callback.InputContactCallBackUpdateHandler;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedCallbackException;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InputContactCallBackUpdateHandlerImpl implements InputContactCallBackUpdateHandler {

    private final UserDataCacheFacade userDataCache;

    private final ContactKeyboardHelperImpl contactKeyboardHelper;

    private final CallbackQueryHandlerContext context;

    @PostConstruct
    @Override
    public void registerInContext() {
        context.addToDataContext(CallBackButtonUtils.EMAIL, this);
        context.addToDataContext(CallBackButtonUtils.PHONE, this);
        context.addToDataContext(CallBackButtonUtils.NAME, this);
        context.addToDataContext(CallBackButtonUtils.SHARED, this);
    }

    @Override
    public EditMessageReplyMarkup processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.debug("Process [callback: {}] from contact keyboard [user id: {}]", data, userId);
        return switch (data) {
            case CallBackButtonUtils.EMAIL -> {
                userDataCache.setUserBotState(userId, BotState.AWAIT_EMAIL);
                yield contactKeyboardHelper.getEditMessageReplyForChosenCallbackButton(update, CallBackButtonUtils.EMAIL);
            }
            case CallBackButtonUtils.PHONE -> {
                userDataCache.setUserBotState(userId, BotState.AWAIT_PHONE);
                yield contactKeyboardHelper.getEditMessageReplyForChosenCallbackButton(update, CallBackButtonUtils.PHONE);
            }
            case CallBackButtonUtils.SHARED -> {
                userDataCache.setUserBotState(userId, BotState.AWAIT_SHARED);
                yield contactKeyboardHelper.getEditMessageReplyForChosenCallbackButton(update, CallBackButtonUtils.SHARED);
            }
            case CallBackButtonUtils.NAME -> {
                userDataCache.setUserBotState(userId, BotState.AWAIT_NAME);
                yield contactKeyboardHelper.getEditMessageReplyForChosenCallbackButton(update, CallBackButtonUtils.NAME);
            }
            default ->
                    throw new UnexpectedCallbackException(MessageUtils.ANOTHER_ACTION, update.getCallbackQuery().getId());
        };
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE);
    }

}
