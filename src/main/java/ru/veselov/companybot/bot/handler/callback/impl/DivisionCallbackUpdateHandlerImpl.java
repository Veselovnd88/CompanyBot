package ru.veselov.companybot.bot.handler.callback.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateHandlerContext;
import ru.veselov.companybot.bot.handler.callback.DivisionCallbackUpdateHandler;
import ru.veselov.companybot.bot.keyboard.DivisionKeyboardHelper;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DivisionCallbackUpdateHandlerImpl implements DivisionCallbackUpdateHandler {

    private final UserDataCacheFacade userDataCacheFacade;

    private final DivisionKeyboardHelper divisionKeyboardHelper;

    private final BotStateHandlerContext context;

    @PostConstruct
    @Override
    public void registerInContext() {
        for (var s : getAvailableStates()) {
            context.add(s, this);
        }
    }

    @Override
    public SendMessage processUpdate(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long userId = callbackQuery.getFrom().getId();
        String data = callbackQuery.getData();
        Map<String, DivisionModel> cachedDivisions = divisionKeyboardHelper.getCachedDivisions();
        DivisionModel division = cachedDivisions.get(data);//div always should be in cache
        userDataCacheFacade.createInquiry(userId, division);
        userDataCacheFacade.setUserBotState(userId, BotState.AWAIT_MESSAGE);
        return SendMessage.builder().chatId(userId)
                .text(MessageUtils.INVITATION_TO_INPUT_INQUIRY).build();
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(BotState.AWAIT_DIVISION_FOR_INQUIRY);
    }

}
