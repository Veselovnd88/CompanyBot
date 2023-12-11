package ru.veselov.companybot.bot.handler.callback.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.handler.callback.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.cache.UserDataCacheFacade;

import java.util.Set;

/**
 * Class for handling updates containing CallBacks for contact managing;
 *
 * @see UserDataCacheFacade
 * @see ContactKeyboardHelperImpl
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactCallbackUpdateHandlerImpl implements ContactCallbackUpdateHandler {

    private final UserDataCacheFacade userDataCache;

    private final ContactKeyboardHelperImpl contactKeyboardHelper;

    private final CallbackQueryDataHandlerContext context;

    @PostConstruct
    @Override
    public void registerInContext() {
        context.add(CallBackButtonUtils.CONTACT, this);
        context.add(CallBackButtonUtils.REPEAT, this);
    }

    @Value("${bot.adminId}")
    private String adminId;

    /**
     * Handle CallbackQuery with {@link CallBackButtonUtils#REPEAT} and {@link  CallBackButtonUtils#CONTACT} data
     * <p>
     * Set up bot state for {@link BotState#AWAIT_CONTACT}
     * </p>
     *
     * @return {@link EditMessageReplyMarkup} editMessageReplyMarkup with keyboard buttons for input contact data
     */
    @Override
    public EditMessageReplyMarkup processUpdate(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.debug("Process [callback: {}] from contact keyboard [user id: {}]", data, userId);
        //reset all and gives new form for filling contact
        userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
        userDataCache.createContact(userId);
        Message message = update.getCallbackQuery().getMessage();
        return EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(contactKeyboardHelper.getContactKeyboard()).build();
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(BotState.READY, BotState.AWAIT_CONTACT, BotState.AWAIT_NAME, BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL, BotState.AWAIT_SHARED, BotState.AWAIT_MESSAGE);
    }

}
