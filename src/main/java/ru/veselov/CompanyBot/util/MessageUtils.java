package ru.veselov.CompanyBot.util;

import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageUtils {
    public static final String MANAGER_DELETED = "Менеджер удален";
    public static final String INPUT_DIV = "Введите код и описание темы/отдела по форме " +
            "<Код>-2 символа <пробел> <Описание>-не более 40 символов";
    public static final String NOT_SUPPORTED_ACTION = "Это действие не поддерживается, или бот ожидает другой команды," +
            " нажмите /info для информации";

    public static Message about;

    public static final String MANAGER_SAVED = "Менеджер сохранен";
    public static String GREETINGS="Здравствуйте, я бот-ассистент компании, я могу принять и передать Ваш запрос " +
            "или заявку на обратный звонок, нажмите /info для просмотра основных функций";
    public static String INFO = """
            О боте:\s
            Бот принимает заявки и перенаправляет их ответственному менеджеру:\s
            /start - начало работы с ботом\s
            /inquiry - отправить заявку\s
            /call - отправить заявку на обратный звонок\s
            /info - справка по основным командам\s
            /about - информация о компании\s
            Отзывы и предложения: https://t.me/VeselovND""";

    public static String CAPTION_TOO_LONG="Текст подписи превышает длину 1024 символа, сократите размер подписи," +
            " или отправьте текст отдельно";
    public static String NO_CUSTOM_EMOJI="Я не поддерживаю кастомные эмодзи";
    public static String CHOOSE_DEP="Выберите направление";
    public static String AWAIT_CONTENT_MESSAGE="Ожидаю сообщений для формирования запроса";
    public static String INPUT_CONTACT = """
            Введите ФИО и контактные данные для обратной связи
            Нажмите на кнопку клавиатуры для ввода информации
            Для передачи заявки мне обязательно нужно Ваше имя и хотя бы 1 контакт для связи
            После заполнения, нажмите сохранить и отправить""";
    public static String SAVED="Запрос сохранен и передан менеджеру на обработку, спасибо!";
    public static String WRONG_CONTACT_FORMAT="Не могу принять контакт в таком формате";
    public static String WRONG_NAME_FORMAT = "Не правильный формат имени, введите ФИО разделенные пробелом";
    public static String NAME_TOO_LONG = "ФИО слишком длинное, не должно превышать 250 знаков";
    public static String WRONG_PHONE = "Неправильный формат номер телефона";
    public static String WRONG_EMAIL="Неправильный формат электронной почты";
    public static String NOT_ENOUGH_CONTACT = "Недостаточно данных для обратной связи (отсутствует e-mail, номер телефона, ФИО)";
    public static final String AWAIT_MANAGER = "Перешлите любое сообщение от менеджера для добавления/удаления";
    public static final String AWAIT_DEPARTMENT="Выберите направление";
    public static String ANOTHER_ACTION = "Ожидаю другое действие, нажмите /start для сброса";
}
