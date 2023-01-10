package ru.veselov.CompanyBot.util;

public class MessageUtils {
    public static String GREETINGS="Здравствуйте, я бот-помощник компании, я могу принять и передать Ваш запрос " +
            "или заявку на обратный звонок";
    public static String NOT_READY = "Бот не готов к работе, нажмите /start для обновления состояния";
    public static String BOT_WAITING_FOR_DPT="Бот ожидает выбор направления для запроса, нажмите /start для сброса";//FIXME
    public static String ABOUT="Информация о компании:";
    public static String INFO = "О боте: \n" +
            "Бот принимает заявки и перенаправляет их ответственному менеджеру: \n" +
            "/start - начало работы с ботом \n" +
            "/inquiry - отправить заявку \n" +
            "/call - отправить заявку на обратный звонок\n" +
            "/info - справка по основным командам\n" +
            "Отзывы и предложения: https://t.me/VeselovND";

    public static String CAPTION_TOO_LONG="Текст подписи превышает длину 1024 символа, сократите размер подписи," +
            " или отправьте текст отдельно";

    public static String NO_CUSTOM_EMOJI="Я не поддерживаю кастомные эмодзи";
    public static String CHOOSE_DEP="Выберите направление";
    public static String UNKNOWN_COMMAND="Неизвестная команда, нажмите /start для сброса";
    public static String CANT_GET_PICTURE="Не удалось сохранить изображение";
    public static String AWAIT_CONTENT_MESSAGE="Ожидаю сообщений для формирования запроса";
    public static String SAVE_MESSAGE="Запрос будет сохранен и передан менеджеру на обработку";
    public static String INPUT_CONTACT = "Введите ФИО и контактные данные для обратной связи или контакт Телеграм";
    public static String SAVED="Запрос сохранен и передан менеджеру на обработку, спасибо!";

    public static String WRONG_CONTACT_FORMAT="Не могу принять контакт в таком формате";


    public static String ERROR="Что-то пошло не так";
}
