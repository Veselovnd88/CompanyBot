package ru.veselov.companybot.bot.handler.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandlerFromContext;
import ru.veselov.companybot.bot.handler.message.CommandUpdateHandler;
import ru.veselov.companybot.bot.handler.message.MessageUpdateHandler;
import ru.veselov.companybot.bot.util.BotUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageUpdateHandlerImpl implements MessageUpdateHandler {

    private static final String LOG_MSG = "Update forwarded to: [{}]";

    private final UserDataCacheFacade userDataCache;

    private final CommandUpdateHandler commandUpdateHandler;

    private final BotStateHandlerContext botStateHandlerContext;

    /**
     * Handler for processing updates contained Message
     *
     * @param update {@link Update} from Telegram
     * @return {@link BotApiMethod} answered message
     */
    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Message message = update.getMessage();
        if (isCommand(message)) {
            log.debug(LOG_MSG, commandUpdateHandler.getClass().getSimpleName());
            return commandUpdateHandler.processUpdate(update);
        }
        String chatId = message.getFrom().getId().toString();
        BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
        UpdateHandlerFromContext handler = botStateHandlerContext.getHandler(botState);
        if (handler != null) {
            BotUtils.validateUpdateHandlerStates(handler, botState, chatId);
            log.debug(LOG_MSG, handler.getClass().getSimpleName());
            return handler.processUpdate(update);
        }
        log.warn("Unexpected action, no handler for message");
        throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
    }

    private boolean isCommand(Message message) {
        log.debug("Checking if message contains command entity");
        if (message.hasEntities() && message.getForwardFrom() == null) {
            Optional<MessageEntity> commandEntity = message.getEntities()
                    .stream().filter(me -> "bot_command".equals(me.getType())).findFirst();
            log.info("Message contains bot_command entity");
            return commandEntity.isPresent();
        }
        log.debug("Message doesn't contains bot_command entity");
        return false;
    }

}
