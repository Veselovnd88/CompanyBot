package ru.veselov.companybot.bot.handler.message.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.context.BotStateMessageHandlerContext;
import ru.veselov.companybot.bot.handler.message.AboutInfoUpdateHandler;
import ru.veselov.companybot.bot.util.UserMessageChecker;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.MessageProcessingException;
import ru.veselov.companybot.service.CompanyInfoService;
import ru.veselov.companybot.util.MessageUtils;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AboutInfoUpdateHandlerImpl implements AboutInfoUpdateHandler {

    private final BotStateMessageHandlerContext context;

    private final CompanyInfoService companyInfoService;

    private final UserMessageChecker userMessageChecker;

    private final UserDataCacheFacade userDataCacheFacade;

    @Override
    @PostConstruct
    public void registerInContext() {
        for (BotState b : getAvailableStates()) {
            context.addToBotStateContext(b, this);
        }
    }

    @Override
    public Set<BotState> getAvailableStates() {
        return Set.of(BotState.AWAIT_INFO);
    }

    /**
     * Receive company information and save to DB
     */
    @Override
    public SendMessage processUpdate(Update update) {
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        if (message.hasText()) {
            String text = message.getText();
            if (text.length() > 900) {
                throw new MessageProcessingException(MessageUtils.INFO_MSG_IS_TOO_LONG, userId.toString());
            }
            userMessageChecker.checkForCustomEmojis(message);
            companyInfoService.save(message);
            MessageUtils.setAbout(message);
            userDataCacheFacade.setUserBotState(userId, BotState.READY);
            return SendMessage.builder()
                    .chatId(userId)
                    .text(MessageUtils.NEW_INFO_MSG)
                    .build();
        }
        throw new MessageProcessingException(MessageUtils.EMPTY_MESSAGE, userId.toString());
    }

}
