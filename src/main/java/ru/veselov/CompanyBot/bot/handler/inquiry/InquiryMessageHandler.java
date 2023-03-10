package ru.veselov.CompanyBot.bot.handler.inquiry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.ArrayList;
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
        Long userId=update.getMessage().getFrom().getId();
        if(userDataCache.getInquiry(userId).getMessages().size()>14){
            SendMessage addContentMessage = askAddContent(userId);
            addContentMessage.setText("Превышено максимальное количество сообщений (15)");
            log.info("{}: превышение макс. количества сообщений",userId);
            return addContentMessage;
        }
        //Проверка на длину подписи (caption) бот не может отправлять сообщение длинней 1024 символов
        if(update.getMessage().getCaption()!=null){
            if(update.getMessage().getCaption().length()>1024){
                log.info("{}: попытка отправить слишком длинной сообщение", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.CAPTION_TOO_LONG,
                        userId.toString());
            }
        }
        //Проверка на кастомные эмодзи
        if(update.getMessage().getEntities()!=null){
            List<MessageEntity> entities = update.getMessage().getEntities();
            if(entities.stream().anyMatch(x->x.getType().equals("custom_emoji"))){
                log.info("{}: попытка отправить кастомные эмодзи", userId);
                throw new NoAvailableActionSendMessageException(MessageUtils.NO_CUSTOM_EMOJI,
                        userId.toString());
            }
        }
        //Сохранение текста с параметрами форматирования
        if(update.getMessage().hasText()){
            Message message = new Message();
            String text = update.getMessage().getText();
            message.setText(text);
            message.setEntities(update.getMessage().getEntities());
            userDataCache.getInquiry(userId).addMessage(message);
            log.info("{}: сохранен текст с разметкой для пользователя",userId);
        }
        if(update.getMessage().hasPhoto()){
            Message message = createMessageWithMedia(update);
            //Получаем список из нескольких вариантов картинки разных размеров
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            PhotoSize photoSize = photoSizes.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(photoSizes.get(photoSizes.size()-1));
            message.setPhoto(List.of(photoSize));
            log.info("{}: сохранил картинку в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if(update.getMessage().hasAudio()){
            Message message = createMessageWithMedia(update);
            Audio audio = update.getMessage().getAudio();
            message.setAudio(audio);
            log.info("{}: сохранил аудио в пост для юзера",userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if(update.getMessage().hasDocument()){
            Message message = createMessageWithMedia(update);
            Document document = update.getMessage().getDocument();
            message.setDocument(document);
            log.info("{}: сохранил документ в пост для юзера",userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if(update.getMessage().hasVideo()){
            Message message = createMessageWithMedia(update);
            Video video = update.getMessage().getVideo();
            message.setVideo(video);
            log.info("{}: сохранил видео в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        if(update.getMessage().hasAnimation()){
            Message message = createMessageWithMedia(update);
            Animation animation = update.getMessage().getAnimation();
            message.setAnimation(animation);
            log.info("{}: сохранил видео в пост для юзера", userId);
            userDataCache.getInquiry(userId).addMessage(message);
        }
        return askAddContent(userId);
    }

    private SendMessage askAddContent(Long userId) {
        InlineKeyboardButton finishMessages = new InlineKeyboardButton();
        finishMessages.setText("Приступить к вводу данных для обратной связи");
        finishMessages.setCallbackData("contact");//-> отсюда сообщение уходит в ContactCallbackHandler
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(finishMessages);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return SendMessage.builder().chatId(userId).text(MessageUtils.AWAIT_CONTENT_MESSAGE)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    private Message createMessageWithMedia(Update update){
        Message message = new Message();
        message.setCaption(update.getMessage().getCaption());
        message.setCaptionEntities(update.getMessage().getCaptionEntities());
        message.setMediaGroupId(update.getMessage().getMediaGroupId());
        return message;
    }
}
