package ru.veselov.CompanyBot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.handler.TelegramFacadeUpdateHandler;
import ru.veselov.CompanyBot.exception.NoAvailableActionCallbackException;
import ru.veselov.CompanyBot.exception.NoAvailableActionException;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.exception.WrongContactException;
import ru.veselov.CompanyBot.util.BotProperties;

import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class CompanyBot extends TelegramLongPollingBot {

    private Long botId;

    private final BotProperties botProperties;

    private final TelegramFacadeUpdateHandler telegramFacadeUpdateHandler;

    public CompanyBot(BotProperties botProperties, TelegramFacadeUpdateHandler telegramFacadeUpdateHandler) {
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
        BotCommandScopeChat botCommandScopeChat = new BotCommandScopeChat();
        botCommandScopeChat.setChatId(Long.valueOf(botProperties.getAdminId()));
        try {
            this.execute(new SetMyCommands(setUpCommands(), new BotCommandScopeDefault(), null));
            log.info("Menu was set up");
            botId = this.getMe().getId();
            log.info("Bot [id: {}]", botId);
        } catch (TelegramApiException e) {
            log.error("Error occurred during starting bot: {}", e.getMessage());
        }
    }


    public void sendMessageWithDelay(SendMessage sendMessage) {
        try {
            this.execute(sendMessage);
            Thread.sleep(30);
        } catch (TelegramApiException e) {
            log.error("Cannot send [message: {}]", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Something went [wrong: {}]", e.getMessage());
        }
    }

    private List<BotCommand> setUpCommands() {
        BotCommand startCommand = new BotCommand("/start", "Приветствие, начало работы с ботом");
        BotCommand inquiryCommand = new BotCommand("/inquiry", "Отправить заявку боту");
        BotCommand callMeCommand = new BotCommand("/call", "Оставить заявку на звонок");
        BotCommand aboutCommand = new BotCommand("/about", "Информация о компании");
        BotCommand infoCommand = new BotCommand("/info", "Информация о боте");
        return List.of(startCommand, inquiryCommand, callMeCommand, aboutCommand, infoCommand);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            //Check speed of sending, limit 30 msg/sec
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                execute(telegramFacadeUpdateHandler.processUpdate(update));
            } catch (NoAvailableActionException e) {
                if (e instanceof WrongContactException) {
                    try {
                        execute(SendMessage.builder().chatId(e.getChatId())
                                .text(e.getMessage()).build());
                    } catch (TelegramApiException ex) {
                        log.error(ex.getMessage());
                    }
                } else if (e instanceof NoAvailableActionSendMessageException) {
                    try {
                        execute(SendMessage.builder().chatId(e.getChatId())
                                .text(e.getMessage()).build());
                    } catch (TelegramApiException ex) {
                        log.error(ex.getMessage());
                    }
                } else if (e instanceof NoAvailableActionCallbackException) {
                    try {
                        execute(AnswerCallbackQuery.builder()
                                .callbackQueryId(e.getChatId())
                                .text(e.getMessage()).build());
                    } catch (TelegramApiException ex) {
                        log.error(ex.getMessage());
                    }
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
