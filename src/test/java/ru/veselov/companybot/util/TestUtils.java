package ru.veselov.companybot.util;

import net.datafaker.Faker;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.DivisionModel;

import java.util.UUID;

public class TestUtils {

    public static Faker faker = new Faker();

    public static Long BOT_ID = 1L;

    public static final Long USER_ID = 2L;

    public static final Long ADMIN_ID = 3L;

    public static final Long CHAT_ID = 4L;

    public static final Integer MESSAGE_ID = 100;

    public static final String CHAT_TITLE = faker.elderScrolls().region();

    public static final String ADMIN_NAME = faker.elderScrolls().dragon();

    public static final String ADMIN_FIRST_NAME = faker.elderScrolls().firstName();

    public static final String ADMIN_LAST_NAME = faker.elderScrolls().lastName();

    public static final String USER_NAME = faker.elderScrolls().creature();

    public static final String USER_FIRST_NAME = faker.elderScrolls().firstName();

    public static final String USER_LAST_NAME = faker.elderScrolls().lastName();

    public static final String USER_PHONE = faker.phoneNumber().phoneNumberNational();

    public static final String CALLBACK_ID = "1000";


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

    public static Contact getUserContact() {
        Contact contact = new Contact();
        contact.setFirstName(USER_FIRST_NAME);
        contact.setUserId(USER_ID);
        contact.setLastName(USER_LAST_NAME);
        contact.setPhoneNumber(USER_PHONE);
        return contact;
    }
}
