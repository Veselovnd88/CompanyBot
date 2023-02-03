package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionException;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.exception.NoDivisionsException;
import ru.veselov.CompanyBot.service.CustomerService;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CommandHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    private final CustomerService customerService;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final ManageKeyboardUtils manageKeyboardUtils;
    @Autowired
    public CommandHandler(UserDataCache userDataCache, ContactCache contactCache, CustomerService customerService, DivisionKeyboardUtils divisionKeyboardUtils, ManageKeyboardUtils manageKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.customerService = customerService;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.manageKeyboardUtils = manageKeyboardUtils;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionException {
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();
        String receivedCommand = update.getMessage().getText();
        log.info("{}: нажата кнопка с командой {}", userId, receivedCommand);
        BotState botState=userDataCache.getUserBotState(userId);
        switch (receivedCommand){
            case "/start":
                if(botState==BotState.BEGIN){
                    customerService.save(user);
                }
                userDataCache.setUserBotState(userId,BotState.READY);
                userDataCache.clear(userId);
                contactCache.clear(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.GREETINGS).build();
            case "/inquiry":
                if(botState==BotState.READY){
                    userDataCache.setUserBotState(userId,BotState.AWAIT_DIVISION_FOR_INQUIRY);
                    return departmentMessageInlineKeyBoard(userId);
                }
                else{
                    throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION,userId.toString());
                }
            case "/call":
                if(botState==BotState.READY){
                    userDataCache.setUserBotState(userId,BotState.AWAIT_CONTACT);
                    return contactMessage(userId);
                }
                else{
                    throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION,userId.toString());
                }

            case "/about":
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.about.getText())
                        .entities(MessageUtils.about.getEntities())
                        .build();
            case "/info":
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INFO).build();

            case "/manage":
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(
                                manageKeyboardUtils.manageKeyboard()).build();
        }
        throw new NoAvailableActionSendMessageException(MessageUtils.NOT_SUPPORTED_ACTION, userId.toString());
    }

    private SendMessage departmentMessageInlineKeyBoard(Long userId){
        InlineKeyboardMarkup customerDivisionKeyboard;
        try {
            customerDivisionKeyboard = divisionKeyboardUtils.getCustomerDivisionKeyboard();
        } catch (NoDivisionsException e) {
            return SendMessage.builder().chatId(userId).text(e.getMessage()).build();
        }
        return SendMessage.builder().chatId(userId).text(MessageUtils.CHOOSE_DEP)
                .replyMarkup(customerDivisionKeyboard).build();
    }

    private SendMessage contactMessage(Long userId) {
        InlineKeyboardButton finishMessages = new InlineKeyboardButton();
        finishMessages.setText("Ввести данные для обратной связи");
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
