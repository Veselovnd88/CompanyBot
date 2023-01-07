package ru.veselov.CompanyBot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.handler.TelegramUpdateHandler;
import ru.veselov.CompanyBot.util.BotProperties;

import java.util.LinkedList;
import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class CompanyBot extends TelegramWebhookBot {

    private Long botId;

    private final TelegramBotsApi telegramBotsApi;
    private final SetWebhook setWebhook;
    private final BotProperties botProperties;
    private final TelegramUpdateHandler telegramUpdateHandler;

    public CompanyBot(TelegramBotsApi telegramBotsApi, SetWebhook setWebhook, BotProperties botProperties, TelegramUpdateHandler telegramUpdateHandler){
        this.telegramBotsApi = telegramBotsApi;
        this.setWebhook = setWebhook;
        this.botProperties = botProperties;
        this.telegramUpdateHandler = telegramUpdateHandler;
        //установка скоупа для команд администратора
        BotCommandScopeChat botCommandScopeChat = new BotCommandScopeChat();
        botCommandScopeChat.setChatId(Long.valueOf(botProperties.getAdminId()));
        try {
            this.telegramBotsApi.registerBot(this, this.setWebhook);
            this.execute(new SetMyCommands(setUpCommands(), new BotCommandScopeDefault(), null));
            this.execute(new SetMyCommands(setUpAdminCommands(),botCommandScopeChat,null));
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
        if(update!=null){
            return telegramUpdateHandler.processUpdate(update);
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return botProperties.getWebHookPath();
    }


    private List<BotCommand> setUpCommands(){
        BotCommand startCommand = new BotCommand("/start","Приветствие, начало работы с ботом");
        BotCommand inquiryCommand = new BotCommand("/inquiry","Отправить заявку боту");
        BotCommand callMeCommand = new BotCommand("/call", "Оставить заявку на звонок");
        BotCommand aboutCommand = new BotCommand("/about", "Информация о компании");
        BotCommand infoCommand = new BotCommand("/info","Информация о боте");
        return List.of(startCommand,inquiryCommand,callMeCommand,infoCommand );
    }

    private List<BotCommand> setUpAdminCommands(){
        BotCommand adminCommand = new BotCommand("/manage","Администрирование");
        List<BotCommand> withStandard = new LinkedList<>(setUpCommands());
        withStandard.add(adminCommand);
        return withStandard;
    }
}
