package ru.veselov.companybot.bot.handler.inquiry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.UpdateHandler;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.exception.NoDivisionsException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.bot.util.MessageUtils;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class DivisionCallbackHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final DivisionKeyboardHelper divisionKeyboardHelper;

    @Autowired
    public DivisionCallbackHandler(UserDataCache userDataCache, DivisionKeyboardHelper divisionKeyboardHelper) {
        this.userDataCache = userDataCache;
        this.divisionKeyboardHelper = divisionKeyboardHelper;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionCallbackException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        Map<UUID, DivisionModel> cachedDivisions;
        try {
            cachedDivisions = divisionKeyboardHelper.getCachedDivisions();
        } catch (NoDivisionsException e) {
            return SendMessage.builder().chatId(userId)
                    .text(e.getMessage()).build();
        }
        DivisionModel division = cachedDivisions.get(UUID.fromString(data));
        if (division != null) {
            userDataCache.createInquiry(userId, division);
            userDataCache.setUserBotState(userId, BotState.AWAIT_MESSAGE);
            return SendMessage.builder().chatId(userId)
                    .text("Введите ваш вопрос или перешлите мне сообщение").build();
        }
        throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION,
                update.getCallbackQuery().getId());
    }
}
