package ru.veselov.companybot.bot.util;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserMessageChecker {

    void checkForLongCaption(Message message);

    void checkForCustomEmojis(Message message);

}
