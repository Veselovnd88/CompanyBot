package ru.veselov.CompanyBot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.util.BotProperties;

@Component
@Getter
@Setter
@Slf4j
public class CompanyBot extends TelegramWebhookBot {

    private Long botId;

    private final TelegramBotsApi telegramBotsApi;
    private final SetWebhook setWebhook;
    private final BotProperties botProperties;

    public CompanyBot(TelegramBotsApi telegramBotsApi, SetWebhook setWebhook, BotProperties botProperties){
        this.telegramBotsApi = telegramBotsApi;
        this.setWebhook = setWebhook;
        this.botProperties = botProperties;
        try {
            this.telegramBotsApi.registerBot(this, this.setWebhook);
           // this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
           // this.execute(new SetMyCommands(commandsAdmin,botCommandScopeChat,null));
            System.out.println(botProperties.getName());
            log.info("Меню установлено");
            botId=this.getMe().getId();
            log.info("Id бота{}", botId);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при запуске бота: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return botProperties.getWebHookPath();
    }
}
