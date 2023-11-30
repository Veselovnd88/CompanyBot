package ru.veselov.companybot.bot.handler.inquiry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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
import ru.veselov.companybot.bot.UpdateHandler;
import ru.veselov.companybot.bot.util.InlineKeyBoardUtils;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.bot.util.MessageUtils;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class InquiryMessageHandler implements UpdateHandler {
    private final UserDataCache userDataCache;

    public InquiryMessageHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionSendMessageException {
        Long userId = update.getMessage().getFrom().getId();
        if (userDataCache.getInquiry(userId).getMessages().size() > 14) {
            SendMessage addContentMessage = askAddContent(userId);
            addContentMessage.setText("Превышено максимальное количество сообщений (15)");
            log.info("{}: превышение макс. количества сообщений", userId);
            return addContentMessage;
        }
        //Проверка на длину подписи (caption) бот не может отправлять сообщение длинней 1024 символов
        if (update.getMessage().getCaption() != null) {
            if (update.getMessage().getCaption().length() > 1024) {
                log.info("{}: попытка отправить слишком длинной сообщение", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.CAPTION_TOO_LONG,
                        userId.toString());
            }
        }
        //Проверка на кастомные эмодзи
        if (update.getMessage().getEntities() != null) {
            List<MessageEntity> entities = update.getMessage().getEntities();
            if (entities.stream().anyMatch(x -> x.getType().equals("custom_emoji"))) {
                log.info("{}: попытка отправить кастомные эмодзи", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.NO_CUSTOM_EMOJI,
                        userId.toString());
            }
        }
        //Сохранение текста с параметрами форматирования
        if (update.getMessage().hasText()) {
            Message message = new Message();
            String text = update.getMessage().getText();
            message.setText(text);
            message.setEntities(update.getMessage().getEntities());
            userDataCache.getInquiry(userId).addMessage(message);
            log.info("{}: сохранен текст с разметкой для пользователя", userId);
        }
        if (update.getMessage().hasPhoto()) {
            Message message = createMessageWithMedia(update);
            //Получаем список из нескольких вариантов картинки разных размеров
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            PhotoSize photoSize = photoSizes.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(photoSizes.get(photoSizes.size() - 1));
            message.setPhoto(List.of(photoSize));
            log.info("{}: сохранил картинку в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasAudio()) {
            Message message = createMessageWithMedia(update);
            Audio audio = update.getMessage().getAudio();
            message.setAudio(audio);
            log.info("{}: сохранил аудио в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasDocument()) {
            Message message = createMessageWithMedia(update);
            Document document = update.getMessage().getDocument();
            message.setDocument(document);
            log.info("{}: сохранил документ в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasVideo()) {
            Message message = createMessageWithMedia(update);
            Video video = update.getMessage().getVideo();
            message.setVideo(video);
            log.info("{}: сохранил видео в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if (update.getMessage().hasAnimation()) {
            Message message = createMessageWithMedia(update);
            Animation animation = update.getMessage().getAnimation();
            message.setAnimation(animation);
            log.info("{}: сохранил видео в пост для юзера", userId);
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
