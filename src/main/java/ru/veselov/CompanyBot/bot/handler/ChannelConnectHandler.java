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
import ru.veselov.CompanyBot.service.ChatService;

@Component
@Slf4j
public class ChannelConnectHandler implements UpdateHandler {
    private final CompanyBot companyBot;
    private final ChatService chatService;
    @Autowired
    public ChannelConnectHandler(CompanyBot companyBot, ChatService chatService) {
        this.companyBot = companyBot;
        this.chatService = chatService;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        User user = update.getMyChatMember().getFrom();
        Long userId = user.getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        User addedUser = update.getMyChatMember().getNewChatMember().getUser();
        Long addedUserId = addedUser.getId();
        if(addedUserId.equals(companyBot.getBotId())){
            Chat chat = update.getMyChatMember().getChat();
            if(newChatMember.getStatus().equals("administrator")){
                chatService.save(chat);
                return SendMessage.builder().chatId(userId)
                        .text("Вы добавили меня к каналу "+chat.getTitle()).build();
            }
            if(newChatMember.getStatus().equals("left")){
                chatService.remove(chat.getId());
                return SendMessage.builder().chatId(userId)
                        .text("Вы удалили меня из канала "+chat.getTitle()).build();

            }
            if(newChatMember.getStatus().equals("kicked")){
                chatService.remove(chat.getId());
                return SendMessage.builder().chatId(userId)
                        .text("Вы кикнули меня с канала "+chat.getTitle()).build();
            }
        }
        log.info("Администратор проводит действия с каналом, в котором есть бот");
        return null;
    }
}
