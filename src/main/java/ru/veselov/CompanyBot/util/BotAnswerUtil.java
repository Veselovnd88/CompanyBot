package ru.veselov.CompanyBot.util;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

public class BotAnswerUtil {

    public static AnswerCallbackQuery getAnswerCallbackErrorMessage(String callbackId){
            return AnswerCallbackQuery.builder().callbackQueryId(callbackId)
            .text(MessageUtils.ERROR)
                .build();}
}
