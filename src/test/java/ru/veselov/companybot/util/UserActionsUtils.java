package ru.veselov.companybot.util;

import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotCommands;

public class UserActionsUtils {


    public static Update userPressStart() {
        return TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.START);
    }

    public static Update userPressCall() {
        return TestUpdates.getUpdateWithMessageWithCommandByUser(BotCommands.CALL);
    }

    public static Update userPressCallbackButton(String callbackData) {
        return TestUpdates.getUpdateWithMessageWithCallbackQueryByUser(callbackData);
    }

    public static Update userSendMessageWithContact(String contactData) {
        return TestUpdates.getUpdateWithMessageWithContactDataByUser(contactData);
    }

    public static Update userAttachedSharedContact(Contact contact) {
        return TestUpdates.getUpdateWithMessageWithSharedContactByUser(contact);
    }

}
