package ru.veselov.companybot.bot.handler.impl;

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
import ru.veselov.companybot.bot.handler.ContactCallbackUpdateHandler;
import ru.veselov.companybot.bot.util.CallBackButtonUtils;
import ru.veselov.companybot.bot.util.ContactMessageProcessor;
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

/**
 * Class for handling updates containing CallBacks for contact managing;
 *
 * @see UserDataCacheFacade
 * @see CustomerService
 * @see ContactMessageProcessor
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactCallbackUpdateHandlerImpl implements ContactCallbackUpdateHandler {

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
        log.debug("Process [callback: {}] from contact keyboard [user id: {}]", data, userId);
        switch (data) {
            case CallBackButtonUtils.EMAIL:
                userDataCache.setUserBotState(userId, BotState.AWAIT_EMAIL);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.EMAIL);
            case CallBackButtonUtils.PHONE:
                userDataCache.setUserBotState(userId, BotState.AWAIT_PHONE);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.PHONE);
            case CallBackButtonUtils.SHARED:
                userDataCache.setUserBotState(userId, BotState.AWAIT_SHARED);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.SHARED);
            case CallBackButtonUtils.NAME:
                userDataCache.setUserBotState(userId, BotState.AWAIT_NAME);
                return keyBoardUtils.editMessageChooseField(update, CallBackButtonUtils.NAME);
            case CallBackButtonUtils.CONTACT, CallBackButtonUtils.REPEAT://went from InquiryMessageHandler
                userDataCache.setUserBotState(userId, BotState.AWAIT_CONTACT);
                userDataCache.createContact(userId);
                return SendMessage.builder().chatId(userId)
                        .text(MessageUtils.INPUT_CONTACT)
                        .replyMarkup(keyBoardUtils.contactKeyBoard())
                        .build();
            case CallBackButtonUtils.SAVE:
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
            default:
                throw new IllegalStateException("Unexpected value: " + data);
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
