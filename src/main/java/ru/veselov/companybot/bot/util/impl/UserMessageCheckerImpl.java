package ru.veselov.companybot.bot.util.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.veselov.companybot.bot.util.BotUtils;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.exception.MessageProcessingException;
import ru.veselov.companybot.util.MessageUtils;

import java.util.List;

@Component
@Slf4j
public class UserMessageCheckerImpl implements UserMessageChecker {

    @Value("${bot.caption-length}")
    private Integer captionLength;

    /**
     * Check if message caption is long than its supported
     *
     * @param message {@link Message} message to check
     * @throws MessageProcessingException if caption longer than we can support
     */
    @Override
    public void checkForLongCaption(Message message) {
        Long userId = message.getFrom().getId();
        if (message.getCaption() != null && (message.getCaption().length() > captionLength)) {
            log.warn("Try to send too long (> {} symbols) caption by [user with id: {}]", captionLength, userId);
            throw new MessageProcessingException(MessageUtils.CAPTION_TOO_LONG, userId.toString());
        }
    }

    /**
     * Check if message contains custom emoji which not supported by our bot
     *
     * @param message {@link Message} message to check
     * @throws MessageProcessingException if message contains custom emoji entity
     */
    @Override
    public void checkForCustomEmojis(Message message) {
        Long userId = message.getFrom().getId();
        if (message.getEntities() != null) {
            List<MessageEntity> entities = message.getEntities();
            if (entities.stream().anyMatch(x -> x.getType().equals(BotUtils.CUSTOM_EMOJI))) {
                log.warn("Try to send custom emojis by [user with id: {}], not supported", userId);
                throw new MessageProcessingException(MessageUtils.NO_CUSTOM_EMOJI, userId.toString());
            }
        }
    }

}
