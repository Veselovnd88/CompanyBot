package ru.veselov.CompanyBot.util;

public class MessageUtils {
    public static String GREETINGS="Здравствуйте, я бот-помощник компании, я могу принять и передать Ваш запрос " +
            "или заявку на обратный звонок";
    public static String NOT_READY = "Бот не готов к работе, нажмите /start для обновления состояния";
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
    public static String CANT_GET_PICTURE="Не удалось сохранить изображение";
    public static String AWAIT_CONTENT_MESSAGE="Ожидаю сообщений для формирования запроса";
}
