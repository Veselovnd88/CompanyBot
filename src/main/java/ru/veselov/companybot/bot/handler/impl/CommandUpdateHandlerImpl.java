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
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.service.CustomerService;

/**
 * Class for handling updates containing commands {@link  BotCommands}:
 * <p>
 * {@link BotCommands#START},{@link BotCommands#INQUIRY},{@link BotCommands#CALL},{@link BotCommands#INFO}
 * {@link BotCommands#ABOUT}
 * </p>
 *
 * @see UserDataCacheFacade
 * @see CustomerService
 * @see DivisionKeyboardHelper
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommandUpdateHandlerImpl implements CommandUpdateHandler {

    private final UserDataCacheFacade userDataCacheFacade;

    private final CustomerService customerService;

    private final DivisionKeyboardHelper divisionKeyboardHelper;

    /**
     * Processing update from Telegram depends on command from {@link BotCommands},
     * after successful processing set up to according {@link BotState}
     *
     * @return {@link SendMessage} message for sending to user to Telegram
     * @throws WrongBotStateException if entering with wrong BotState
     */
    @Override
    public SendMessage processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();
        String receivedCommand = update.getMessage().getText();
        log.debug("[Command {}] button pressed by user [id: {}]", receivedCommand, userId);
        BotState botState = userDataCacheFacade.getUserBotState(userId);
        switch (receivedCommand) {
            case BotCommands.START:
                if (botState == BotState.BEGIN) {
                    customerService.save(user);//TODO make async
                }
                userDataCacheFacade.setUserBotState(userId, BotState.READY);
                userDataCacheFacade.clear(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.GREETINGS).build();
            case BotCommands.INQUIRY:
                if (botState == BotState.READY) {
                    userDataCacheFacade.setUserBotState(userId, BotState.AWAIT_DIVISION_FOR_INQUIRY);
                    return departmentMessageInlineKeyBoard(userId);
                } else {
                    throw new WrongBotStateException(MessageUtils.ANOTHER_ACTION, userId.toString());
                }
            case BotCommands.CALL:
                if (botState == BotState.READY) {
                    userDataCacheFacade.setUserBotState(userId, BotState.AWAIT_CONTACT);
                    return contactMessage(userId);
                } else {
                    throw new WrongBotStateException(MessageUtils.ANOTHER_ACTION, userId.toString());
                }
            case BotCommands.ABOUT:
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.getAbout().getText())
                        .entities(MessageUtils.getAbout().getEntities())
                        .build();
            case BotCommands.INFO:
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INFO).build();
            default:
                throw new WrongBotStateException(MessageUtils.NOT_SUPPORTED_ACTION, userId.toString());
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
