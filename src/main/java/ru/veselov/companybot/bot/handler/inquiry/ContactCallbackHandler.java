package ru.veselov.companybot.bot.handler.inquiry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.UpdateHandler;
import ru.veselov.companybot.bot.util.KeyBoardUtils;
import ru.veselov.companybot.bot.util.MessageUtils;
import ru.veselov.companybot.cache.UserDataCacheFacade;
import ru.veselov.companybot.exception.NoAvailableActionCallbackException;
import ru.veselov.companybot.exception.NoAvailableActionException;
import ru.veselov.companybot.exception.NoAvailableActionSendMessageException;
import ru.veselov.companybot.exception.NoSuchDivisionException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.service.CustomerService;
import ru.veselov.companybot.service.impl.InquiryServiceImpl;
import ru.veselov.companybot.service.impl.SenderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactCallbackHandler implements UpdateHandler {

    private final CompanyBot bot;

    private final UserDataCacheFacade userDataCache;

    private final CustomerService customerService;

    private final InquiryServiceImpl inquiryService;

    private final SenderService senderService;

    private final KeyBoardUtils keyBoardUtils;

    @Value("${bot.adminId}")
    private String adminId;


    @Override
    public BotApiMethod<?> processUpdate(Update update) throws NoAvailableActionException {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        log.info("{}: меню ввода контактов через Callback", userId);
        switch (data) {
            case "email":
                userDataCache.setUserBotState(userId, BotState.AWAIT_EMAIL);
                return keyBoardUtils.editMessageChooseField(update, "email");
            case "phone":
                userDataCache.setUserBotState(userId, BotState.AWAIT_PHONE);
                return keyBoardUtils.editMessageChooseField(update, "phone");
            case "shared":
                userDataCache.setUserBotState(userId, BotState.AWAIT_SHARED);
                return keyBoardUtils.editMessageChooseField(update, "shared");
            case "name":
                userDataCache.setUserBotState(userId, BotState.AWAIT_NAME);
                return keyBoardUtils.editMessageChooseField(update, "name");
            case "contact"://приходит из InquiryMessageHandler
            case "repeat":
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                userDataCache.createContact(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .replyMarkup(keyBoardUtils.contactKeyBoard())
                        .build();
            case "save":
                if (checkIsContactOK(userDataCache.getContact(userId))) {
                    customerService.saveContact(userDataCache.getContact(userId));
                    if (userDataCache.getInquiry(userId) != null) {
                        inquiryService.save(userDataCache.getInquiry(userId));
                    }
                    try {
                        senderService.send(userDataCache.getInquiry(userId), userDataCache.getContact(userId));
                    } catch (TelegramApiException | NoSuchDivisionException e) {
                        log.error(e.getMessage());
                        log.error("{}: не удалось отправить сообщение пользователя", userId);
                        try {
                            bot.execute(SendMessage.builder().chatId(adminId)
                                    .text("Не удалось отправить сообщение пользователя").build());
                        } catch (TelegramApiException ex) {
                            log.error("Не удалось отправить сообщение об ошибке администратору");
                        }
                    } finally {
                        keyBoardUtils.clear(userId);
                        userDataCache.clear(userId);
                    }
                    return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                            .text(MessageUtils.SAVED).showAlert(true)
                            .build();
                } else {
                    throw new NoAvailableActionSendMessageException(MessageUtils.NOT_ENOUGH_CONTACT,
                            userId.toString());
                }
        }
        throw new NoAvailableActionCallbackException(MessageUtils.ANOTHER_ACTION, update.getCallbackQuery().getId());
    }

    private boolean checkIsContactOK(ContactModel contact) {
        if (contact.getLastName() == null && contact.getFirstName() == null && contact.getSecondName() == null) {
            return false;
        }
        return contact.getEmail() != null || contact.getPhone() != null || contact.getContact() != null;
    }

}
