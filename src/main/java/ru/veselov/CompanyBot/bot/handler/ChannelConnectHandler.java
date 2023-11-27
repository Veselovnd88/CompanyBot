package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.service.impl.ChatServiceImpl;
import ru.veselov.CompanyBot.util.MessageUtils;

@Component
@Slf4j
public class ChannelConnectHandler implements UpdateHandler {
    private final CompanyBot companyBot;
    private final ChatServiceImpl chatServiceImpl;
    @Autowired
    public ChannelConnectHandler(CompanyBot companyBot, ChatServiceImpl chatServiceImpl) {
        this.companyBot = companyBot;
        this.chatServiceImpl = chatServiceImpl;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionSendMessageException {
        User user = update.getMyChatMember().getFrom();
        Long userId = user.getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        User addedUser = update.getMyChatMember().getNewChatMember().getUser();
        Long addedUserId = addedUser.getId();
        if(addedUserId.equals(companyBot.getBotId())){
            Chat chat = update.getMyChatMember().getChat();
            if(newChatMember.getStatus().equalsIgnoreCase("administrator")){
                chatServiceImpl.save(chat);
                log.info("{}: бот добавлен в канал {}",userId,chat.getTitle());
                return SendMessage.builder().chatId(userId)
                        .text("Вы добавили меня к каналу "+chat.getTitle()).build();
            }
            if(newChatMember.getStatus().equalsIgnoreCase("left")){
                chatServiceImpl.remove(chat.getId());
                log.info("{}: бот удален из канала {}",userId,chat.getTitle());
                return SendMessage.builder().chatId(userId)
                        .text("Вы удалили меня из канала "+chat.getTitle()).build();

            }
            if(newChatMember.getStatus().equalsIgnoreCase("kicked")){
                chatServiceImpl.remove(chat.getId());
                log.info("{}: бот кикнут из канала {}",userId,chat.getTitle());
                return SendMessage.builder().chatId(userId)
                        .text("Вы кикнули меня с канала "+chat.getTitle()).build();
            }
        }
        log.info("{}: не поддерживаемое действие с каналом", userId);
        throw new NoAvailableActionSendMessageException(MessageUtils.ANOTHER_ACTION,userId.toString());
    }
}
