package ru.veselov.companybot.bot.util.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.bot.BotConstant;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.util.TestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class UserMessageCheckerImplTest {

    private final static Integer CAPTION_LENGTH = 1024;

    UserMessageCheckerImpl userMessageChecker;

    Message message;

    User user;

    @BeforeEach
    void init() {
        message = spy(Message.class);
        user = spy(User.class);
        message.setFrom(user);
        user.setId(TestUtils.USER_ID);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(0);
        messageEntity.setLength(0);
        message.setEntities(List.of(messageEntity));
        userMessageChecker = new UserMessageCheckerImpl();
        ReflectionTestUtils.setField(userMessageChecker, "captionLength", CAPTION_LENGTH, Integer.class);
    }

    @Test
    void shouldThrowExceptionForTooLongCaptionMessage() {
        message.setCaption("i".repeat(CAPTION_LENGTH + 1));//create too long string
        Assertions.assertThatThrownBy(() -> userMessageChecker.checkForLongCaption(message)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @Test
    void shouldPassForNormalCaptionMessage() {
        message.setCaption("i");
        Assertions.assertThatNoException().isThrownBy(
                () -> userMessageChecker.checkForLongCaption(message)
        );
    }

    @Test
    void shouldThrowExceptionIfCustomEmojiDetected() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType(BotConstant.CUSTOM_EMOJI);
        message.setEntities(List.of(messageEntity));

        Assertions.assertThatThrownBy(() -> userMessageChecker.checkForCustomEmojis(message)
        ).isInstanceOf(NoAvailableActionSendMessageException.class);
    }

    @Test
    void shouldPassIfNoCustomEmojis() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("no emojis");
        message.setEntities(List.of(messageEntity));
        Assertions.assertThatNoException().isThrownBy(
                () -> userMessageChecker.checkForCustomEmojis(message)
        );
    }

    @Test
    void shouldPassIfNoEntities() {
        message.setEntities(null);
        Assertions.assertThatNoException().isThrownBy(
                () -> userMessageChecker.checkForCustomEmojis(message)
        );
    }

    @Test
    void shouldPassForEmptyList() {
        message.setEntities(Collections.emptyList());
        Assertions.assertThatNoException().isThrownBy(
                () -> userMessageChecker.checkForCustomEmojis(message)
        );
    }

}

