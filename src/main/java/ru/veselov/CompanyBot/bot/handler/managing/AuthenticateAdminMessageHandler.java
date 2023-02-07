package ru.veselov.CompanyBot.bot.handler.managing;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.exception.NoAvailableActionException;
import ru.veselov.CompanyBot.exception.NoAvailableActionSendMessageException;
import ru.veselov.CompanyBot.util.ManageKeyboardUtils;

@Component
@Slf4j
public class AuthenticateAdminMessageHandler implements UpdateHandler {
    @Setter
    @Value("${bot.admin_pass}")
    private String pass;
    private final BCryptPasswordEncoder encoder;
    private final ManageKeyboardUtils manageKeyboardUtils;
    private final UserDataCache userDataCache;
    @Autowired
    public AuthenticateAdminMessageHandler(BCryptPasswordEncoder encoder, ManageKeyboardUtils manageKeyboardUtils, UserDataCache userDataCache) {
        this.encoder = encoder;
        this.manageKeyboardUtils = manageKeyboardUtils;
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionException {
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        if(update.getMessage().hasText()){
            String text = update.getMessage().getText();
            if(encoder.matches(text,pass)){
                userDataCache.setUserBotState(userId, BotState.MANAGE);
                return SendMessage.builder().chatId(userId)
                        .text("Режим управления").replyMarkup(
                                manageKeyboardUtils.manageKeyboard()).build();
            }
            else{
                return SendMessage.builder().chatId(userId)
                        .text("Повторите ввод, или нажмите /start для сброса").build();
            }
        }
        throw new NoAvailableActionSendMessageException("Сообщение не содержит текст", userId.toString());
    }
}
