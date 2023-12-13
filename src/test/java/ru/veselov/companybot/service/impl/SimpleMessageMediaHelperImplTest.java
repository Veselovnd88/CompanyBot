package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.mapper.impl.SendMediaMapperImpl;
import ru.veselov.companybot.util.TestUtils;

import java.util.LinkedHashMap;
import java.util.Map;

class SimpleMessageMediaHelperImplTest {

    SimpleMessageMediaHelperImpl simpleMessageMediaHelper;

    @BeforeEach
    void init() {
        SendMediaMapperImpl sendMediaMapper = new SendMediaMapperImpl();
        simpleMessageMediaHelper = new SimpleMessageMediaHelperImpl(sendMediaMapper);
    }

    @Test
    void shouldReturnTenMessages() {
        Map<Integer, Message> messages = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            if (i / 2 == 0) {
                messages.put(i, TestUtils.getTextMessage("text %s".formatted(i)));
            } else {
                messages.put(i, TestUtils.getPhotoMessage());
            }
        }
        Map<Integer, PartialBotApiMethod<?>> resultMap
                = simpleMessageMediaHelper.convertSendMediaMessage(messages, "100");

        Assertions.assertThat(resultMap).hasSize(10);
        Assertions.assertThat(resultMap.get(1)).isInstanceOf(SendMessage.class);
        Assertions.assertThat(resultMap.get(2)).isInstanceOf(SendPhoto.class);

        Assertions.assertThatNoException().isThrownBy(
                () -> {
                    SendMessage sendMessage = (SendMessage) resultMap.get(1);
                }
        );
        SendMessage sendMessage = (SendMessage) resultMap.get(1);
        Assertions.assertThat(sendMessage.getText()).isEqualTo("text 1");
    }

}
