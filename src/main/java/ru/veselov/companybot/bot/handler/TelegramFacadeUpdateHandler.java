package ru.veselov.companybot.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.handler.callback.CallbackQueryUpdateHandler;
import ru.veselov.companybot.bot.handler.message.MessageUpdateHandler;
import ru.veselov.companybot.exception.handler.BotExceptionToMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramFacadeUpdateHandler {

    private static final String LOG_MSG = "Update forwarded to: [{}]";

    @Value("${bot.adminId}")
    private String adminId;

    private final ChannelConnectUpdateHandler channelConnectUpdateHandler;

    private final CallbackQueryUpdateHandler callbackQueryUpdateHandler;

    private final MessageUpdateHandler messageUpdateHandler;

    /**
     * Method checking content of update and define handler
     *
     * @param update {@link Update} from Telegram
     * @return {@link BotApiMethod} message for further executing
     */
    @BotExceptionToMessage
    public BotApiMethod<?> processUpdate(Update update) {
        //updates for connecting bot to chat
        if (update.hasMyChatMember()) {
            if (update.getMyChatMember().getFrom().getId().toString().equals(adminId)) {
                log.debug(LOG_MSG, channelConnectUpdateHandler.getClass().getSimpleName());
                return channelConnectUpdateHandler.processUpdate(update);
            } else {
                log.debug("Not admin tried to connect bot to his channel");
                return SendMessage.builder().chatId(update.getMyChatMember().getFrom().getId())
                        .text("Я работаю только в тех каналах, куда меня добавил администратор")
                        .build();
            }
        }
        if (update.hasMessage()) {
            log.debug(LOG_MSG, messageUpdateHandler.getClass().getSimpleName());
            return messageUpdateHandler.processUpdate(update);
        }
        if (update.hasCallbackQuery()) {
            log.debug(LOG_MSG, callbackQueryUpdateHandler.getClass().getSimpleName());
            return callbackQueryUpdateHandler.processUpdate(update);
        }
        //no answer if not supported Update received
        return null;
    }

}
