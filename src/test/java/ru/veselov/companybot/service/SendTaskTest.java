package ru.veselov.companybot.service;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.exception.CriticalBotException;
import ru.veselov.companybot.service.sender.SendTask;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

class SendTaskTest {

    SendTask sendTask;

    CompanyBot botMock;

    @BeforeEach
    void init() {
        botMock = Mockito.mock(CompanyBot.class);
        Chat chat = new Chat();
        chat.setId(-100L);
        List<PartialBotApiMethod<?>> messagesToSend = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            messagesToSend.add(SendMessage.builder().chatId(chat.getId()).text("I am message number %s".formatted(i))
                    .build());
        }
        sendTask = new SendTask(botMock, chat, messagesToSend);
    }

    @Test
    @SneakyThrows
    void testShouldSendAllMessagesWithDelay() {
        Instant now = Instant.now();
        sendTask.run();
        Instant after = Instant.now();
        Assertions.assertThat(after.toEpochMilli() - now.toEpochMilli()).isGreaterThan(500);
        Mockito.verify(botMock, Mockito.times(100)).execute(Mockito.any(SendMessage.class));
    }

    @Test
    @SneakyThrows
    void shouldThrowException() {
        Mockito.when(botMock.execute(Mockito.any(SendMessage.class))).thenThrow(TelegramApiException.class);
        Assertions.assertThatThrownBy(() -> sendTask.run())
                .isInstanceOf(CriticalBotException.class);
    }

}
