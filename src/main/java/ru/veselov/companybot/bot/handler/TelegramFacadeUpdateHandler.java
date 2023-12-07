package ru.veselov.companybot.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.HandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandler;
import ru.veselov.companybot.bot.handler.impl.ChannelConnectUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.impl.CommandUpdateHandlerImpl;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.exception.NoAvailableActionException;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramFacadeUpdateHandler {

    @Value("${bot.adminId}")
    private String adminId;

    private final CommandUpdateHandlerImpl commandHandler;

    private final ChannelConnectUpdateHandlerImpl channelConnectUpdateHandlerImpl;

    private final HandlerContext handlerContext;

    private final UserDataCacheFacade userDataCache;

    private final CallbackQueryHandlerContext callbackQueryHandlerContext;

    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionException {
        //updates for connecting bot to chat
        if (update.hasMyChatMember()) {
            if (update.getMyChatMember().getFrom().getId().toString().equals(adminId)) {
                return channelConnectUpdateHandlerImpl.processUpdate(update);
            } else {
                return SendMessage.builder().chatId(update.getMyChatMember().getFrom().getId())
                        .text("Я работаю только в тех каналах, куда меня добавил администратор")
                        .build();
            }
        }

        if (update.hasMessage() && isCommand(update)) {
            return commandHandler.processUpdate(update);
        }

        if (update.hasMessage()) {
            BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
            if (handlerContext.isInMessageContext(botState)) {
                return handlerContext.getMessageHandler(botState).processUpdate(update);
            }
            throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION,
                    update.getMessage().getFrom().getId().toString());
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            UpdateHandler handler = callbackQueryHandlerContext.getHandler(callbackData);
            if (handler == null) {
                throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION, update.getCallbackQuery()
                        .getId());
            }
            BotState botState = userDataCache.getUserBotState(update.getCallbackQuery().getFrom().getId());
            if (!handler.getAvailableStates().contains(botState)) {
                throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION, update.getCallbackQuery().getId());
            }
            return handler.processUpdate(update);
        }
        return null;
    }


    private boolean isCommand(Update update) {
        /*additional checking if message is not forwarded*/
        if (update.hasMessage() && update.getMessage().hasEntities() && update.getMessage().getForwardFrom() == null) {
            Optional<MessageEntity> commandEntity = update.getMessage().getEntities()
                    .stream().filter(x -> "bot_command".equals(x.getType())).findFirst();
            return commandEntity.isPresent();
        }
        return false;
    }

}
