package ru.veselov.CompanyBot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class BotAnswerUtil {

    public  AnswerCallbackQuery getAnswerCallbackErrorMessage(String callbackId){
            return AnswerCallbackQuery.builder().callbackQueryId(callbackId)
            .text(MessageUtils.ERROR)
                .build();}

    public  AnswerCallbackQuery getAnswerCallbackAnotherAction(String callbackId){
        return AnswerCallbackQuery.builder().callbackQueryId(callbackId)
                .text(MessageUtils.ANOTHER_ACTION)
                .build();}

    public SendMessage getAnswerNotSupportMessage(String chatId){
        return SendMessage.builder().chatId(chatId)
                .text(MessageUtils.UNKNOWN_COMMAND).build();
    }

    public SendMessage getAnswerAwaitManager(Long chatId){
        return SendMessage.builder().chatId(chatId)
                .text(MessageUtils.AWAIT_MANAGER).build();
    }
}
