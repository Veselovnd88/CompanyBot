package ru.veselov.companybot.util;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestUtils {

    public static final UUID DIVISION_ID = UUID.randomUUID();

    public static Faker faker = new Faker();

    public static final String DIVISION_DESC = faker.elderScrolls().creature();

    public static Long BOT_ID = 1L;

    public static final Long USER_ID = new Random().nextLong();

    public static final Long ADMIN_ID = 3L;

    public static final Long CHAT_ID = 4L;

    public static final Integer MESSAGE_ID = 100;

    public static final String CHAT_TITLE = faker.elderScrolls().region();

    public static final String ADMIN_NAME = faker.elderScrolls().dragon();

    public static final String ADMIN_FIRST_NAME = faker.elderScrolls().firstName().replace(" ", "");

    public static final String ADMIN_LAST_NAME = faker.elderScrolls().lastName().replace(" ", "");

    public static final String USER_NAME = faker.elderScrolls().creature().replace(" ", "");

    public static final String USER_FIRST_NAME = faker.elderScrolls().firstName().replace(" ", "");

    public static final String USER_LAST_NAME = faker.elderScrolls().lastName().replace(" ", "");

    public static final String USER_PHONE = faker.phoneNumber().phoneNumberNational();

    public static final String USER_EMAIL = "evil@hate.com";

    public static final String CALLBACK_ID = "1000";

    public static final String MEDIA_GROUP_ID = "100000";


    public static final String DIVISION_NAME = "NAME";

    public static DivisionModel getDivision() {
        return DivisionModel.builder()
                .divisionId(UUID.randomUUID())
                .name(DIVISION_NAME)
                .build();
    }

    public static User getAdminUser() {
        User user = new User();
        user.setId(ADMIN_ID);
        user.setUserName(ADMIN_NAME);
        user.setFirstName(ADMIN_FIRST_NAME);
        user.setLastName(ADMIN_LAST_NAME);
        return user;
    }

    public static User getSimpleUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUserName(USER_NAME);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        return user;
    }

    public static User getBotUser() {
        User user = new User();
        user.setId(BOT_ID);
        user.setIsBot(true);
        return user;
    }

    public static Chat getChat(Long chatId, String chatTitle) {
        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setTitle(chatTitle);
        return chat;
    }

    public static Message getUserMessageForCallback() {
        Message message = new Message();
        message.setMessageId(MESSAGE_ID);
        message.setFrom(getSimpleUser());
        message.setChat(getChat(USER_ID, USER_NAME));
        return message;
    }

    public static ContactModel getUserContactModel() {
        return new ContactModel(
                USER_LAST_NAME,
                USER_FIRST_NAME,
                null,
                USER_PHONE,
                "123@123.com",
                null,
                USER_ID
        );
    }

    public static InquiryModel getInquiryModel() {
        InquiryModel inquiryModel = new InquiryModel();
        inquiryModel.setUserId(USER_ID);
        inquiryModel.setDivision(getDivision());
        inquiryModel.setMessages(List.of(getTextMessage("test")));
        return inquiryModel;
    }

    public static Contact getUserContact() {
        Contact contact = new Contact();
        contact.setFirstName(USER_FIRST_NAME);
        contact.setUserId(USER_ID);
        contact.setLastName(USER_LAST_NAME);
        contact.setPhoneNumber(USER_PHONE);
        return contact;
    }

    public static Message getMessageWithGroupAndPhoto(String mediaGroupId, String fileId) {
        Message message = new Message();
        message.setFrom(getSimpleUser());
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId(fileId);
        message.setMediaGroupId(mediaGroupId);
        message.setPhoto(List.of(photoSize));
        return message;
    }

    public static Message getTextMessage(String text) {
        Message message = new Message();
        message.setMessageId(MESSAGE_ID);
        message.setFrom(getSimpleUser());
        message.setText(text);
        return message;
    }

    public static Message getPhotoMessage() {
        Message message = new Message();
        message.setFrom(getSimpleUser());
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId("11234");
        message.setPhoto(List.of(photoSize));
        return message;
    }

    public static SendMessage checkSendMessageInstanceAndCast(BotApiMethod<?> botApiMethod) {
        Assertions.assertThat(botApiMethod).as("Check if answer is SendMessage instance")
                .isInstanceOf(SendMessage.class);
        return (SendMessage) botApiMethod;
    }

    public static DivisionDTO getDivisionDTO() {
        return new DivisionDTO(
                "div",
                faker.elderScrolls().dragon()
        );
    }

    public static DivisionEntity getDivisionEntity() {
        return DivisionEntity.builder()
                .name(DIVISION_NAME)
                .description(DIVISION_DESC)
                .divisionId(DIVISION_ID)
                .build();
    }

}
