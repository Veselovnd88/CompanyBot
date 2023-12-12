package ru.veselov.companybot.util;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberBanned;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberLeft;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;

import java.util.List;

public class TestUpdates {

    public static Update getUpdateWithConnectionToChannelByAdmin() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setChat(getChat(TestUtils.ADMIN_ID));
        chatMemberUpdated.setFrom(getAdminUser());
        update.setMyChatMember(chatMemberUpdated);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithAdministratorStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberAdministrator chatMemberAdministrator = new ChatMemberAdministrator();
        chatMemberAdministrator.setUser(getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberAdministrator);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithLeftStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberLeft chatMemberLeft = new ChatMemberLeft();
        chatMemberLeft.setUser(getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberLeft);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithKickedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberBanned chatMemberBanned = new ChatMemberBanned();
        chatMemberBanned.setUser(getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberBanned);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithUnsupportedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberRestricted unsupportedChatMember = new ChatMemberRestricted();
        unsupportedChatMember.setUser(getBotUser());
        update.getMyChatMember().setNewChatMember(unsupportedChatMember);
        return update;
    }

    public static Update getUpdateWithConnectionNoBotWithUnsupportedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberAdministrator chatMemberAdministrator = new ChatMemberAdministrator();
        chatMemberAdministrator.setUser(getSimpleUser());
        update.getMyChatMember().setNewChatMember(chatMemberAdministrator);
        return update;
    }

    public static Update getUpdateWithConnectionBotToChannelByUser() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setFrom(getSimpleUser());
        update.setMyChatMember(chatMemberUpdated);
        return update;
    }

    public static Update getUpdateWithMessageWithCommandByUser(String command) {
        Update update = new Update();
        Message message = new Message();
        MessageEntity botCommandEntity = new MessageEntity();
        botCommandEntity.setType("bot_command");
        botCommandEntity.setOffset(0);
        botCommandEntity.setLength(command.length());
        message.setEntities(List.of(botCommandEntity));
        message.setText(command);
        update.setMessage(message);
        message.setFrom(getSimpleUser());
        return update;
    }


    public static User getAdminUser() {
        User user = new User();
        user.setId(TestUtils.ADMIN_ID);
        user.setUserName(TestUtils.ADMIN_NAME);
        user.setFirstName(TestUtils.ADMIN_FIRST_NAME);
        user.setLastName(TestUtils.ADMIN_LAST_NAME);
        return user;
    }

    public static User getSimpleUser() {
        User user = new User();
        user.setId(TestUtils.USER_ID);
        user.setUserName(TestUtils.USER_NAME);
        user.setFirstName(TestUtils.USER_FIRST_NAME);
        user.setLastName(TestUtils.USER_LAST_NAME);
        return user;
    }

    public static User getBotUser() {
        User user = new User();
        user.setId(TestUtils.BOT_ID);
        user.setIsBot(true);
        return user;
    }

    public static Chat getChat(Long chatId) {
        Chat chat = new Chat();
        chat.setId(chatId);
        return chat;
    }

}
