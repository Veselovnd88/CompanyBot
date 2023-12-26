package ru.veselov.companybot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.handler.TelegramFacadeUpdateHandler;

import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class CompanyBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;

    private final TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    public CompanyBot(BotProperties botProperties, @Lazy TelegramFacadeUpdateHandler telegramFacadeUpdateHandler) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.telegramFacadeUpdateHandler = telegramFacadeUpdateHandler;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }


    @Override
    public void onRegister() {
        super.onRegister();
        try {
            this.execute(new SetMyCommands(setUpCommands(), new BotCommandScopeDefault(), null));
            log.info("Menu was set up");
            Long botId = this.getMe().getId();
            botProperties.setBotId(botId);
            log.info("Bot [id: {}]", botId);
        } catch (TelegramApiException e) {
            log.error("Error occurred during starting bot: {}", e.getMessage());
        }
    }

    private List<BotCommand> setUpCommands() {
        BotCommand startCommand = new BotCommand(BotCommands.START, "Приветствие, начало работы с ботом");
        BotCommand inquiryCommand = new BotCommand(BotCommands.INQUIRY, "Отправить заявку боту");
        BotCommand callMeCommand = new BotCommand(BotCommands.CALL, "Оставить заявку на звонок");
        BotCommand aboutCommand = new BotCommand(BotCommands.ABOUT, "Информация о компании");
        BotCommand infoCommand = new BotCommand(BotCommands.INFO, "Информация о боте");
        return List.of(startCommand, inquiryCommand, callMeCommand, aboutCommand, infoCommand);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            //Check speed of sending, limit 30 msg/sec
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Problem with delay sending of message: error: {}", e.getMessage());
            }
            try {
                execute(telegramFacadeUpdateHandler.processUpdate(update));
            } catch (TelegramApiException e) {
                log.error("Can't execute answer message, error: {}", e.getMessage());
            }

        }
    }

}
