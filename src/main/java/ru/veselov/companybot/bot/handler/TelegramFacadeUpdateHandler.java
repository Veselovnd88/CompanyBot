package ru.veselov.companybot.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.util.BotStateUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.exception.handler.BotExceptionToMessage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramFacadeUpdateHandler {

    @Value("${bot.adminId}")
    private String adminId;

    private final CommandUpdateHandler commandUpdateHandler;

    private final ChannelConnectUpdateHandler channelConnectUpdateHandler;

    private final UserDataCacheFacade userDataCache;

    private final CallbackQueryUpdateHandler callbackQueryUpdateHandler;

    private final BotStateHandlerContext botStateHandlerContext;

    @BotExceptionToMessage
    public BotApiMethod<?> processUpdate(Update update) {
        //updates for connecting bot to chat
        if (update.hasMyChatMember()) {
            if (update.getMyChatMember().getFrom().getId().toString().equals(adminId)) {
                log.debug("Update forwarded to channelConnectUpdateHandler");
                return channelConnectUpdateHandler.processUpdate(update);
            } else {
                log.debug("Not admin tried to connect bot to his channel");
                return SendMessage.builder().chatId(update.getMyChatMember().getFrom().getId())
                        .text("Я работаю только в тех каналах, куда меня добавил администратор")
                        .build();
            }
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (isCommand(message)) {
                log.debug("Update forwarded to commandUpdateHandler");
                return commandUpdateHandler.processUpdate(update);
            }
        }
        if (update.hasMessage()) {
            String chatId = update.getMessage().getFrom().getId().toString();
            BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
            UpdateHandlerFromContext handler = botStateHandlerContext.getHandler(botState);
            if (handler != null) {
                BotStateUtils.validateUpdateHandlerStates(handler, botState, chatId);
                return handler.processUpdate(update);
            }
            throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
        }
        if (update.hasCallbackQuery()) {
            return callbackQueryUpdateHandler.processUpdate(update);
        }
        return null;
    }

    private boolean isCommand(Message message) {
        log.debug("Checking if message contains command entity");
        if (message.hasEntities() && message.getForwardFrom() == null) {
            Optional<MessageEntity> commandEntity = message.getEntities()
                    .stream().filter(me -> "bot_command".equals(me.getType())).findFirst();
            log.debug("Message contains bot_command entity");
            return commandEntity.isPresent();
        }
        log.debug("Message doesn't contains bot_command entity");
        return false;
    }

}
