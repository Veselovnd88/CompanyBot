package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.handler.InquiryMessageUpdateHandler;
import ru.veselov.companybot.bot.util.InlineKeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.model.InquiryModel;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryMessageUpdateHandlerImpl implements InquiryMessageUpdateHandler {

    @Value("${bot.max-messages}")
    private Integer maxMessages;

    private static final String CACHED_FOR_USER_ID = "%s cached for [user id: {}]";

    private final UserDataCacheFacade userDataCacheFacade;

    private final UserMessageChecker userMessageChecker;

    @Override
    public SendMessage processUpdate(Update update) {
        Message receivedMessage = update.getMessage();
        Long userId = receivedMessage.getFrom().getId();
        InquiryModel userCachedInquiry = userDataCacheFacade.getInquiry(userId);
        if (userCachedInquiry.getMessages().size() > maxMessages) {
            SendMessage addContentMessage = askAddContactData(userId);
            addContentMessage.setText("Превышено максимальное количество сообщений (%s)".formatted(maxMessages + 1));
            log.warn("Max qnt of messages exceed for [user id: {}]", userId);
            return addContentMessage;
        }
        userMessageChecker.checkForLongCaption(receivedMessage);
        userMessageChecker.checkForCustomEmojis(receivedMessage);
        //Saving text with formatting
        if (receivedMessage.hasText()) {
            Message message = new Message();
            String text = receivedMessage.getText();
            message.setText(text);
            message.setEntities(receivedMessage.getEntities());
            userCachedInquiry.addMessage(message);
            log.info(CACHED_FOR_USER_ID.formatted("Text with markUp"), userId);
        }
        if (receivedMessage.hasPhoto()) {
            Message message = createMessageWithMedia(update);
            //List of several images
            List<PhotoSize> photoSizes = receivedMessage.getPhoto();
            PhotoSize photoSize = photoSizes.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(photoSizes.get(photoSizes.size() - 1));
            message.setPhoto(List.of(photoSize));
            log.info(CACHED_FOR_USER_ID.formatted("Image"), userId);
            userCachedInquiry.addMessage(message);
        }
        if (receivedMessage.hasAudio()) {
            Message message = createMessageWithMedia(update);
            Audio audio = receivedMessage.getAudio();
            message.setAudio(audio);
            log.info(CACHED_FOR_USER_ID.formatted("Audio"), userId);
            userCachedInquiry.addMessage(message);
        }
        if (receivedMessage.hasDocument()) {
            Message message = createMessageWithMedia(update);
            Document document = receivedMessage.getDocument();
            message.setDocument(document);
            log.info(CACHED_FOR_USER_ID.formatted("Document"), userId);
            userCachedInquiry.addMessage(message);
        }
        if (receivedMessage.hasVideo()) {
            Message message = createMessageWithMedia(update);
            Video video = receivedMessage.getVideo();
            message.setVideo(video);
            log.info(CACHED_FOR_USER_ID.formatted("Video"), userId);
            userCachedInquiry.addMessage(message);
        }
        if (receivedMessage.hasAnimation()) {
            Message message = createMessageWithMedia(update);
            Animation animation = receivedMessage.getAnimation();
            message.setAnimation(animation);
            log.info(CACHED_FOR_USER_ID.formatted("Animation"), userId);
            userCachedInquiry.addMessage(message);
        }
        return askAddContactData(userId);
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return null;
    }

    private SendMessage askAddContactData(Long userId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyBoardUtils
                .setUpContactInlineKeyboard("Приступить к вводу данных для обратной связи");
        return SendMessage.builder().chatId(userId).text(MessageUtils.AWAIT_CONTENT_MESSAGE)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    private Message createMessageWithMedia(Update update) {
        Message message = new Message();
        message.setCaption(update.getMessage().getCaption());
        message.setCaptionEntities(update.getMessage().getCaptionEntities());
        message.setMediaGroupId(update.getMessage().getMediaGroupId());
        return message;
    }

}