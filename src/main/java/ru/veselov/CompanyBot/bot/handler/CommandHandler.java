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
    @Autowired
    public CommandHandler(UserDataCache userDataCache, ContactCache contactCache, CustomerService customerService, DivisionKeyboardUtils divisionKeyboardUtils) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.customerService = customerService;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
    }
    /*Класс обрабатывает все апдейты, который содержат команды*/
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();
        String receivedCommand = update.getMessage().getText();
        log.info("{}: нажата команда {}", userId, receivedCommand);
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
                    userDataCache.setUserBotState(userId,BotState.AWAIT_DEPARTMENT);
                    return departmentMessageInlineKeyBoard(userId);
                }
                else{
                    return SendMessage.builder().chatId(userId)
                            .text(MessageUtils.NOT_READY).build();
                }
            case "/call":
                if(botState==BotState.READY){
                    userDataCache.setUserBotState(userId,BotState.AWAIT_CONTACT);
                    return contactMessage(userId);
                }
                else{
                    return SendMessage.builder().chatId(userId)
                            .text(MessageUtils.NOT_READY).build();
                }

            case "/about":
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.ABOUT).build();
            case "/info":
                return SendMessage.builder().chatId(userId)//TODO ссылку на компанию в callback кнопке
                        .text(MessageUtils.INFO).build();

            case "/manage":
                /*TODO сделать еще команды для администрирования
                *  Управление отделами-удалить, добавить, редактировать (всё как обычно)
                * Управление менеджерами
                * -удалить менеджера - с выдачей всех манагеров по 5 строк например
                * -добавить менеджера - реализовано*/
                userDataCache.setUserBotState(userId,BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(
                                ManageKeyboardUtils.manageKeyboard()).build();
                /*userDataCache.setUserBotState(userId,BotState.AWAIT_MANAGER);
                return SendMessage.builder().chatId(userId)
                            .text(MessageUtils.AWAIT_MANAGER).build();*/
        }
        return SendMessage.builder().chatId(userId)
                .text(MessageUtils.UNKNOWN_COMMAND).build();
    }



    private SendMessage departmentMessageInlineKeyBoard(Long userId){
        InlineKeyboardMarkup customerDivisionKeyboard = divisionKeyboardUtils.getCustomerDivisionKeyboard();
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
