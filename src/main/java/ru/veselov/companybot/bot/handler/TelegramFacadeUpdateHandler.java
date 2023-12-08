package ru.veselov.companybot.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.context.CallbackQueryDataHandlerContext;
import ru.veselov.companybot.bot.context.UpdateHandler;
import ru.veselov.companybot.bot.handler.impl.ChannelConnectUpdateHandlerImpl;
import ru.veselov.companybot.bot.handler.impl.CommandUpdateHandlerImpl;
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

    private final CommandUpdateHandlerImpl commandHandler;

    private final ChannelConnectUpdateHandlerImpl channelConnectUpdateHandlerImpl;

    private final UserDataCacheFacade userDataCache;

    private final CallbackQueryDataHandlerContext callbackQueryDataHandlerContext;

    private final BotStateHandlerContext botStateHandlerContext;

    @BotExceptionToMessage
    public BotApiMethod<?> processUpdate(Update update) {
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
            String chatId = update.getMessage().getFrom().getId().toString();
            BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
            UpdateHandler handler = botStateHandlerContext.getHandler(botState);
            if (handler != null) {
                validateUpdateHandlerStates(handler, botState, chatId);
                return handler.processUpdate(update);
            }
            throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
        }

        if (update.hasCallbackQuery()) {
            //check for handler in data context
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            BotState botState = userDataCache.getUserBotState(callbackQuery.getFrom().getId());
            String chatId = callbackQuery.getId();

            UpdateHandler updateHandler;

            updateHandler = callbackQueryDataHandlerContext.getHandler(callbackData);
            if (updateHandler != null) {
                validateUpdateHandlerStates(updateHandler, botState, chatId);
                return updateHandler.processUpdate(update);
            } else {
                updateHandler = botStateHandlerContext.getHandler(botState);
                if (updateHandler != null) {
                    validateUpdateHandlerStates(updateHandler, botState, chatId);
                    return updateHandler.processUpdate(update);
                } else {
                    throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
                }
            }
        }
        return null;
    }

    private void validateUpdateHandlerStates(UpdateHandler updateHandler, BotState botState, String chatId) {
        if (!updateHandler.getAvailableStates().contains(botState)) {
            throw new UnexpectedActionException(MessageUtils.ANOTHER_ACTION, chatId);
        }
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
