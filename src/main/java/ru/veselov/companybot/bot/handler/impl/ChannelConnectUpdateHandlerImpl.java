package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.companybot.bot.BotInfo;
import ru.veselov.companybot.bot.handler.ChannelConnectUpdateHandler;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.service.impl.ChatServiceImpl;
import ru.veselov.companybot.util.MessageUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelConnectUpdateHandlerImpl implements ChannelConnectUpdateHandler {

    private static final String ADMINISTRATOR = "administrator";
    private static final String LEFT = "left";
    private static final String KICKED = "kicked";

    private final ChatServiceImpl chatServiceImpl;

    @Override
    public SendMessage processUpdate(Update update) throws NoAvailableActionSendMessageException {
        User user = update.getMyChatMember().getFrom();
        Long userId = user.getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        User addedUser = update.getMyChatMember().getNewChatMember().getUser();
        Long addedUserId = addedUser.getId();
        if (addedUserId.equals(BotInfo.botId)) {
            Chat chat = update.getMyChatMember().getChat();
            if (newChatMember.getStatus().equalsIgnoreCase(ADMINISTRATOR)) {
                chatServiceImpl.save(chat);
                log.info("Bot added to [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы добавили меня к каналу " + chat.getTitle()).build();
            }
            if (newChatMember.getStatus().equalsIgnoreCase(LEFT)) {
                chatServiceImpl.remove(chat.getId());
                log.info("Bot was removed from [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы удалили меня из канала " + chat.getTitle()).build();

            }
            if (newChatMember.getStatus().equalsIgnoreCase(KICKED)) {
                chatServiceImpl.remove(chat.getId());
                log.info("Bot was kicked from [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы кикнули меня с канала " + chat.getTitle()).build();
            }
        }
        log.error("Not supported action for [user: {}]", userId);
        throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION, userId.toString());
    }
}
