package ru.veselov.companybot.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.companybot.bot.BotConstant;
import ru.veselov.companybot.bot.BotProperties;
import ru.veselov.companybot.bot.handler.ChannelConnectUpdateHandler;
import ru.veselov.companybot.service.ChatService;

/**
 * Class for handling updates containing logic of connecting/disconnecting to channels
 *
 * @see ChatService
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelConnectUpdateHandlerImpl implements ChannelConnectUpdateHandler {

    private final ChatService chatService;

    private final BotProperties botProperties;

    /**
     * Processing update from Telegram with connecting/disconnecting bot event,
     * checking if user has botId, and send message to this chat;
     * <p>
     * Ignores events if not bot was connected to channel
     *
     * @return {@link SendMessage} message for sending to chat/user to Telegram
     */
    @Override
    public SendMessage processUpdate(Update update) {
        User user = update.getMyChatMember().getFrom();
        Long userId = user.getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        User addedUser = update.getMyChatMember().getNewChatMember().getUser();
        Long addedUserId = addedUser.getId();
        if (addedUserId.equals(botProperties.getBotId())) {
            Chat chat = update.getMyChatMember().getChat();
            String chatMemberStatus = newChatMember.getStatus();
            if (chatMemberStatus.equalsIgnoreCase(BotConstant.ADMINISTRATOR)) {
                chatService.save(chat);
                log.info("Bot added to [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы добавили меня к каналу " + chat.getTitle()).build();
            }
            if (chatMemberStatus.equalsIgnoreCase(BotConstant.LEFT)) {
                chatService.remove(chat.getId());
                log.info("Bot was removed from [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы удалили меня из канала " + chat.getTitle()).build();
            }
            if (chatMemberStatus.equalsIgnoreCase(BotConstant.KICKED)) {
                chatService.remove(chat.getId());
                log.info("Bot was kicked from [channel: {} by user: {}]", chat.getTitle(), userId);
                return SendMessage.builder().chatId(userId)
                        .text("Вы кикнули меня с канала " + chat.getTitle()).build();
            }
            log.error("Not supported action for [user: {}]", userId);
        }
        //ignore another connection and disconnections
        return null;
    }
}
