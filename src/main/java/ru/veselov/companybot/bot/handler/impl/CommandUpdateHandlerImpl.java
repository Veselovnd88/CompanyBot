package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.CommandUpdateHandler;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.bot.util.InlineKeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.CustomerService;

/**
 * Handling Updates containing bot commands
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommandUpdateHandlerImpl implements CommandUpdateHandler {

    private static final String BOT_STATE_IS_FOR_USER_ID_LOG = "Bot state is {} for user [id: {}]";

    private final UserDataCache userDataCache;

    private final ContactCache contactCache;

    private final CustomerService customerService;

    private final DivisionKeyboardHelper divisionKeyboardHelper;

    @Override
    public SendMessage processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();
        String receivedCommand = update.getMessage().getText();
        log.debug("[Command {}] button pressed by user [id: {}]", receivedCommand, userId);
        BotState botState = userDataCache.getUserBotState(userId);
        log.debug(BOT_STATE_IS_FOR_USER_ID_LOG, botState, userId);
        switch (receivedCommand) {
            case BotCommands.START:
                if (botState == BotState.BEGIN) {
                    customerService.save(user);//TODO make async
                }
                userDataCache.setUserBotState(userId, BotState.READY);
                userDataCache.clear(userId);
                contactCache.clear(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.GREETINGS).build();
            case BotCommands.INQUIRY:
                if (botState == BotState.READY) {
                    userDataCache.setUserBotState(userId, BotState.AWAIT_DIVISION_FOR_INQUIRY);
                    return departmentMessageInlineKeyBoard(userId);
                } else {
                    throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION, userId.toString());
                }
            case BotCommands.CALL:
                if (botState == BotState.READY) {
                    userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                    return contactMessage(userId);
                } else {
                    throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION, userId.toString());
                }
            case BotCommands.ABOUT:
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.getABOUT().getText())
                        .entities(MessageUtils.getABOUT().getEntities())
                        .build();
            case BotCommands.INFO:
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INFO).build();
            default:
                throw new NoAvailableActionSendMessageException(MessageUtils.NOT_SUPPORTED_ACTION, userId.toString());
        }
    }

    private SendMessage departmentMessageInlineKeyBoard(Long userId) {
        InlineKeyboardMarkup customerDivisionKeyboard;
        customerDivisionKeyboard = divisionKeyboardHelper.getCustomerDivisionKeyboard();
        return SendMessage.builder().chatId(userId).text(MessageUtils.CHOOSE_DEP)
                .replyMarkup(customerDivisionKeyboard).build();
    }

    private SendMessage contactMessage(Long userId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyBoardUtils
                .setUpContactInlineKeyboard("Нажмите для ввода данных для обратной связи");
        return SendMessage.builder().chatId(userId).text(MessageUtils.INPUT_CONTACT)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

}
