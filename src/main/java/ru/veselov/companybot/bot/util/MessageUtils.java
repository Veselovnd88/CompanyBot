package ru.veselov.companybot.bot.util;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.model.ContactModel;

public class MessageUtils {

    public static final String NOT_SUPPORTED_ACTION = "Это действие не поддерживается, или бот ожидает другой команды," +
            " нажмите /info для информации";
    public static final String COMMON_DIV = "Общие вопросы";
    public static final String INPUT_FIO = "Ввести ФИО";
    public static final String INPUT_EMAIL = "Ввести email";
    public static final String INPUT_PHONE = "Ввести номер телефона";
    public static final String ATTACH_CONTACT = "Прикрепить контакт";
    public static final String SAVE_AND_SEND = "Сохранить и отправить контактные данные";

    @Getter
    @Setter
    private static Message about;

    public static final String GREETINGS = "Здравствуйте, я бот-ассистент компании, я могу принять и передать Ваш запрос " +
            "или заявку на обратный звонок, нажмите /info для просмотра основных функций";
    public static final String INFO = """
            О боте:\s
            Бот принимает заявки и перенаправляет их ответственному менеджеру:\s
            /start - начало работы с ботом\s
            /inquiryEntity - отправить заявку\s
            /call - отправить заявку на обратный звонок\s
            /info - справка по основным командам\s
            /about - информация о компании\s
            Отзывы и предложения: https://t.me/VeselovND""";

    public static final String CAPTION_TOO_LONG = "Текст подписи превышает длину 1024 символа, сократите размер подписи,"
            +
            " или отправьте текст отдельно";

    public static final String NO_CUSTOM_EMOJI = "Я не поддерживаю кастомные эмодзи";

    public static final String CHOOSE_DEP = "Выберите тематику";

    public static final String AWAIT_CONTENT_MESSAGE = "Ожидаю сообщение для формирования запроса";

    public static final String INPUT_CONTACT = """
            Введите ФИО и контактные данные для обратной связи
            Нажмите на кнопку клавиатуры для ввода информации
            Для передачи заявки мне обязательно нужно Ваше имя и хотя бы 1 контакт для связи
            После заполнения, нажмите сохранить и отправить""";
    public static final String SAVED = "Запрос сохранен и передан менеджеру на обработку, спасибо!";

    public static final String WRONG_CONTACT_FORMAT = "Не могу принять контакт в таком формате";

    public static final String WRONG_NAME_FORMAT = "Не правильный формат имени, введите ФИО разделенные пробелом";

    public static final String NAME_TOO_LONG = "ФИО слишком длинное, не должно превышать 250 знаков";

    public static final String WRONG_PHONE = "Неправильный формат номер телефона";

    public static final String WRONG_EMAIL = "Неправильный формат электронной почты";

    public static final String NOT_ENOUGH_CONTACT = "Недостаточно данных для обратной связи (отсутствует e-mail, номер телефона, ФИО)";

    public static final String ANOTHER_ACTION = "Ожидаю другое действие, нажмите /start для сброса";

    public static final String INVITATION_TO_INPUT_INQUIRY = "Введите ваш вопрос или перешлите мне сообщение";

    private MessageUtils() {
    }

    public static String createContactMessage(ContactModel contact, boolean hasInquiry) {
        StringBuilder sb = new StringBuilder();
        sb.append(contact.getLastName() == null ? "" : (contact.getLastName() + " "));
        /*if (contact.getLastName() != null) {
            sb.append(contact.getLastName()).append(" ");
        }*/
        if (contact.getFirstName() != null) {
            sb.append(contact.getFirstName()).append(" ");
        }
        sb.append(contact.getFirstName() == null ? "" : (contact.getFirstName() + " "));
        /*if (contact.getSecondName() != null) {
            sb.append(contact.getSecondName()).append(" ");
        }*/
        sb.append(contact.getLastName() == null ? "" : ("\nТелефон: " + contact.getLastName()));
        /*if (contact.getPhone() != null) {
            sb.append("\nТелефон: ").append(contact.getPhone());
        }*/
        sb.append(contact.getEmail() == null ? "" : ("\nЭл. почта: " + contact.getEmail()));
        /*if (contact.getEmail() != null) {
            sb.append("\nЭл. почта: ").append(contact.getEmail());
        }*/
        String prefix;
        if (hasInquiry) {
            prefix = "Контактное лицо для связи: ";
        } else {
            prefix = "Направлена заявка на обратный звонок\nКонтактное лицо для связи: ";
        }
        return prefix + sb.toString().trim();
    }
}
