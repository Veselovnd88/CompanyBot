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
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.exception.NoDivisionsException;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.bot.util.MessageUtils;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class DivisionCallbackHandler implements UpdateHandler {
    private final UserDataCacheFacade userDataCacheFacade;
    private final DivisionKeyboardHelper divisionKeyboardHelper;

    @Autowired
    public DivisionCallbackHandler(UserDataCacheFacade userDataCacheFacade, DivisionKeyboardHelper divisionKeyboardHelper) {
        this.userDataCacheFacade = userDataCacheFacade;
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
            userDataCacheFacade.createInquiry(userId, division);
            userDataCacheFacade.setUserBotState(userId, BotState.AWAIT_MESSAGE);
            return SendMessage.builder().chatId(userId)
                    .text("Введите ваш вопрос или перешлите мне сообщение").build();
        }
        throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION,
                update.getCallbackQuery().getId());
    }
}
