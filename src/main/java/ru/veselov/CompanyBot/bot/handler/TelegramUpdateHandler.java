package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.UpdateHandler;

import java.util.Optional;

@Component
@Slf4j
public class TelegramUpdateHandler implements UpdateHandler {
    private final CommandHandler commandHandler;
    @Autowired
    public TelegramUpdateHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        if(update.hasMessage()&&isCommand(update)){
            return commandHandler.processUpdate(update);
        }

        return null;
    }


    private boolean isCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasEntities()) {
            Optional<MessageEntity> commandEntity = update.getMessage().getEntities()
                    .stream().filter(x -> "bot_command".equals(x.getType())).findFirst();
            return commandEntity.isPresent();
        }
        return false;
    }
}
