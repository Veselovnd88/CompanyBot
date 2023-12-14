package ru.veselov.companybot.service.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
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

    private final List<PartialBotApiMethod<?>> messagesToSend;

    @Override
    public void run() {
        Long chatId = chat.getId();
        log.debug("Starting task for sending messages to chat: {}", chatId);
        for (PartialBotApiMethod<?> msg : messagesToSend) {
            try {
                if (msg instanceof BotApiMethod<?> botApiMethod) {
                    bot.execute(botApiMethod);
                }
                else if (msg instanceof SendPhoto sendPhoto)
                    bot.execute(sendPhoto);
                else if (msg instanceof SendDocument sendDocument) {
                    bot.execute(sendDocument);
                }
                else if (msg instanceof SendVideo sendVideo) {
                    bot.execute(sendVideo);
                }
                else if (msg instanceof SendAudio sendAudio) {
                    bot.execute(sendAudio);
                }
                else if (msg instanceof SendAnimation sendAnimation) {
                    bot.execute(sendAnimation);
                }
                else if (msg instanceof SendVoice sendVoice) {
                    bot.execute(sendVoice);
                }
                else if (msg instanceof SendMediaGroup sendMediaGroup) {
                    bot.execute(sendMediaGroup);
                } else {
                    log.warn("Attempt to send unsupported message type");
                    bot.execute(SendMessage.builder()
                            .chatId(chatId).text("Попытка отправить неподдерживаемое сообщение").build());
                }
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
