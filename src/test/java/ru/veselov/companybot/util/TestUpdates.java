package ru.veselov.companybot.util;

import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberBanned;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberLeft;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;
import org.telegram.telegrambots.meta.api.objects.games.Animation;

import java.util.List;
import java.util.UUID;

public class TestUpdates {

    public static final String STUB_TEXT = "my text";

    public static Update getUpdateWithConnectionToChannelByAdmin() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setChat(TestUtils.getChat(TestUtils.CHAT_ID, TestUtils.CHAT_TITLE));
        chatMemberUpdated.setFrom(TestUtils.getAdminUser());
        update.setMyChatMember(chatMemberUpdated);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithAdministratorStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberAdministrator chatMemberAdministrator = new ChatMemberAdministrator();
        chatMemberAdministrator.setUser(TestUtils.getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberAdministrator);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithLeftStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberLeft chatMemberLeft = new ChatMemberLeft();
        chatMemberLeft.setUser(TestUtils.getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberLeft);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithKickedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberBanned chatMemberBanned = new ChatMemberBanned();
        chatMemberBanned.setUser(TestUtils.getBotUser());
        update.getMyChatMember().setNewChatMember(chatMemberBanned);
        return update;
    }

    public static Update getUpdateWithConnectionBotWithUnsupportedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberRestricted unsupportedChatMember = new ChatMemberRestricted();
        unsupportedChatMember.setUser(TestUtils.getBotUser());
        update.getMyChatMember().setNewChatMember(unsupportedChatMember);
        return update;
    }

    public static Update getUpdateWithConnectionNoBotWithUnsupportedStatusToChannelByAdmin() {
        Update update = getUpdateWithConnectionToChannelByAdmin();
        ChatMemberAdministrator chatMemberAdministrator = new ChatMemberAdministrator();
        chatMemberAdministrator.setUser(TestUtils.getSimpleUser());
        update.getMyChatMember().setNewChatMember(chatMemberAdministrator);
        return update;
    }

    public static Update getUpdateWithConnectionBotToChannelByUser() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setFrom(TestUtils.getSimpleUser());
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
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageWithTextContentByUser() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT + UUID.randomUUID());
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageWithTextContentByAdmin() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT + UUID.randomUUID());
        update.setMessage(message);
        message.setFrom(TestUtils.getAdminUser());
        return update;
    }

    public static Update getUpdateWithMessageWithPhotoByUser() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT);
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileSize(100);
        message.setPhoto(List.of(photoSize));
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageNoCommandNoEntitiesWithAudioByUser() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT);
        Audio audio = new Audio();
        message.setAudio(audio);
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageNoCommandNoEntitiesWithDocumentByUser() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT);
        Document document = new Document();
        message.setDocument(document);
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageNoCommandNoEntitiesWithAnimationByUser() {
        Update update = new Update();
        Message message = new Message();
        message.setText(STUB_TEXT);
        Animation animation = new Animation();
        message.setAnimation(animation);
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageWithCallbackQueryByUser(String callbackData) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setFrom(TestUtils.getSimpleUser());
        callbackQuery.setData(callbackData);
        callbackQuery.setId(TestUtils.CALLBACK_ID);
        callbackQuery.setMessage(TestUtils.getUserMessageForCallback());
        update.setCallbackQuery(callbackQuery);
        return update;
    }

    public static Update getUpdateWithMessageWithContactDataByUser(String contactData) {
        Update update = new Update();
        Message message = new Message();
        message.setText(contactData);
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

    public static Update getUpdateWithMessageWithSharedContactByUser(Contact contact) {
        Update update = new Update();
        Message message = new Message();
        message.setContact(contact);
        message.setEntities(null);
        update.setMessage(message);
        message.setFrom(TestUtils.getSimpleUser());
        return update;
    }

}
