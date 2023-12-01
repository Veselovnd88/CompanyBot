package ru.veselov.companybot.bot.util.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.veselov.companybot.bot.BotConstant;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;

import java.util.List;

@Component
@Slf4j
public class UserMessageCheckerImpl implements UserMessageChecker {

    @Value("${bot.caption-length}")
    private Integer captionLength;

    @Override
    public void checkForLongCaption(Message message) {
        Long userId = message.getFrom().getId();
        if (message.getCaption() != null && (message.getCaption().length() > captionLength)) {
            log.warn("Try to send too long (> {} symbols) caption by [user with id: {}]", captionLength, userId);
            throw new NoAvailableActionSendMessageException(MessageUtils.CAPTION_TOO_LONG,
                    userId.toString());
        }
    }

    @Override
    public void checkForCustomEmojis(Message message) {
        Long userId = message.getFrom().getId();
        if (message.getEntities() != null) {
            List<MessageEntity> entities = message.getEntities();
            if (entities.stream().anyMatch(x -> x.getType().equals(BotConstant.CUSTOM_EMOJI))) {
                log.info("Try to send custom emojis by [user with id: {}], not supported", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.NO_CUSTOM_EMOJI,
                        userId.toString());
            }
        }
    }

}
