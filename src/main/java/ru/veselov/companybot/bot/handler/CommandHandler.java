package ru.veselov.companybot.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.companybot.bot.BotCommands;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.util.DivisionKeyboardUtils;
import ru.veselov.companybot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler implements CommandUpdateHandler {

    public static final String BOT_STATE_IS_FOR_USER_ID_LOG = "Bot state is {} for user [id: {}]";
    private final UserDataCache userDataCache;

    private final ContactCache contactCache;

    private final CustomerService customerService;

    private final DivisionKeyboardUtils divisionKeyboardUtils;

    @Override
    public SendMessage processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();
        String receivedCommand = update.getMessage().getText();
        log.info("{}: pressed button with command {}", userId, receivedCommand);
        BotState botState = userDataCache.getUserBotState(userId);
        log.info(BOT_STATE_IS_FOR_USER_ID_LOG, botState, userId);
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
                        .text(MessageUtils.about.getText())
                        .entities(MessageUtils.about.getEntities())
                        .build();
            case BotCommands.INFO:
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INFO).build();

        }
        throw new NoAvailableActionSendMessageException(MessageUtils.NOT_SUPPORTED_ACTION, userId.toString());
    }

    private SendMessage departmentMessageInlineKeyBoard(Long userId) {
        InlineKeyboardMarkup customerDivisionKeyboard;
        customerDivisionKeyboard = divisionKeyboardUtils.getCustomerDivisionKeyboard();
        return SendMessage.builder().chatId(userId).text(MessageUtils.CHOOSE_DEP)
                .replyMarkup(customerDivisionKeyboard).build();
    }

    private SendMessage contactMessage(Long userId) {
        InlineKeyboardButton finishMessages = new InlineKeyboardButton();
        finishMessages.setText("Нажмите для ввода данных для обратной связи");
        finishMessages.setCallbackData("contact");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(finishMessages);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return SendMessage.builder().chatId(userId).text(MessageUtils.INPUT_CONTACT)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

}
