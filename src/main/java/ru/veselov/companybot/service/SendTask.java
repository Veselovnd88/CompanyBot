package ru.veselov.companybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.exception.CriticalBotException;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SendTask implements Runnable {

    private final CompanyBot bot;

    private final Chat chat;

    private final List<BotApiMethod<?>> messagesToSend;

    @Override
    public void run() {
        Long chatId = chat.getId();
        log.debug("Starting task for sending messages to chat: {}", chatId);
        for (var msg : messagesToSend) {
            try {
                bot.execute(msg);
                Thread.sleep(5);
            } catch (TelegramApiException | InterruptedException e) {
                log.error("Problem occurred during sending message");
                Thread.currentThread().interrupt();
                throw new CriticalBotException(e.getMessage(), e.getCause());
            }
            log.debug("Message: [{}] successfully sent to chat: [{}]", msg.getClass().getSimpleName(), chatId);
        }
        log.info("Task for sending {} messages to chat: {} performed", messagesToSend.size(), chatId);
    }

}
