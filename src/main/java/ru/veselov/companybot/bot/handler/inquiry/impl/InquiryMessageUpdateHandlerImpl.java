package ru.veselov.companybot.bot.handler.inquiry.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.veselov.companybot.bot.handler.inquiry.InquiryMessageUpdateHandler;
import ru.veselov.companybot.bot.util.InlineKeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryMessageUpdateHandlerImpl implements InquiryMessageUpdateHandler {

    @Value("${bot.max-messages}")
    private Integer maxMessages;

    @Value("${bot.caption-length}")
    private Integer captionLength;

    private static final String SAVED_FOR_USER_ID = "%s saved for [user id: {}]";

    private final UserDataCache userDataCache;

    @Override
    public SendMessage processUpdate(Update update) throws NoAvailableActionSendMessageException {
        Long userId = update.getMessage().getFrom().getId();
        if (userDataCache.getInquiry(userId).getMessages().size() > maxMessages) {
            SendMessage addContentMessage = askAddContent(userId);
            addContentMessage.setText("Превышено максимальное количество сообщений (%s)".formatted(maxMessages + 1));
            log.warn("Max qnt of messages exceed for [user id: {}]", userId);
            return addContentMessage;
        }
        //Try to send caption more than 1024 symbols
        if (update.getMessage().getCaption() != null && (update.getMessage().getCaption().length() > captionLength)) {
            log.warn("Try to send too long (> {} symbols) caption by [user with id: {}]", captionLength, userId);
            throw new NoAvailableActionSendMessageException(MessageUtils.CAPTION_TOO_LONG,
                    userId.toString());
        }
        //Check if custom emojis present
        if (update.getMessage().getEntities() != null) {
            List<MessageEntity> entities = update.getMessage().getEntities();
            if (entities.stream().anyMatch(x -> x.getType().equals("custom_emoji"))) {
                log.info("Try to send custom emojis by [user with id: {}], not supported", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.NO_CUSTOM_EMOJI,
                        userId.toString());
            }
        }
        //Saving text with formatting
        if (update.getMessage().hasText()) {
            Message message = new Message();
            String text = update.getMessage().getText();
            message.setText(text);
            message.setEntities(update.getMessage().getEntities());
            userDataCache.getInquiry(userId).addMessage(message);
            log.info(SAVED_FOR_USER_ID.formatted("Text with markUp"), userId);
        }
        if (update.getMessage().hasPhoto()) {
            Message message = createMessageWithMedia(update);
            //List of several images
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            PhotoSize photoSize = photoSizes.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(photoSizes.get(photoSizes.size() - 1));
            message.setPhoto(List.of(photoSize));
            log.info(SAVED_FOR_USER_ID.formatted("Image"), userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasAudio()) {
            Message message = createMessageWithMedia(update);
            Audio audio = update.getMessage().getAudio();
            message.setAudio(audio);
            log.info(SAVED_FOR_USER_ID.formatted("Audio"), userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasDocument()) {
            Message message = createMessageWithMedia(update);
            Document document = update.getMessage().getDocument();
            message.setDocument(document);
            log.info(SAVED_FOR_USER_ID.formatted("Document"), userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasVideo()) {
            Message message = createMessageWithMedia(update);
            Video video = update.getMessage().getVideo();
            message.setVideo(video);
            log.info(SAVED_FOR_USER_ID.formatted("Video"), userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasAnimation()) {
            Message message = createMessageWithMedia(update);
            Animation animation = update.getMessage().getAnimation();
            message.setAnimation(animation);
            log.info(SAVED_FOR_USER_ID.formatted("Animation"), userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        return askAddContent(userId);
    }

    private SendMessage askAddContent(Long userId) {
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
