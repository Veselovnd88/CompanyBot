package ru.veselov.companybot.exception.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.CriticalBotException;
import ru.veselov.companybot.exception.ProcessUpdateException;
import ru.veselov.companybot.exception.UnexpectedActionException;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.exception.WrongContactException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BotExceptionHandler {

    public static final String EXCEPTION_HANDLED = "Exception handled: {}";
    public static final String SMTH_WENT_WRONG = "Something went wrong during send message to {}, msg: {}";

    private final CompanyBot companyBot;

    private final ContactKeyboardHelperImpl contactKeyboardHelper;


    @Pointcut("@annotation(ru.veselov.companybot.exception.handler.BotExceptionToMessage)")
    public void handledMethods() {
    }

    @AfterThrowing(pointcut = "handledMethods()", throwing = "ex")
    public void handleWrongBotStateExceptionAndConvertToSendMessage(WrongBotStateException ex) {
        convertAndSendMessage(ex);
    }

    @Around(value = "handledMethods()")
    public Object handleContactProcessingExceptionAndConvertToSendMessage(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (ContactProcessingException ex) {
            log.warn("Handled {} exception with message {}", ex.getClass().getSimpleName(), ex.getMessage());
            return SendMessage.builder().chatId(ex.getChatId())
                    .text(ex.getMessage()).replyMarkup(contactKeyboardHelper.getContactKeyboard())
                    .build();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new CriticalBotException(SMTH_WENT_WRONG, ex);
        }
    }

    @AfterThrowing(pointcut = "handledMethods()", throwing = "ex")
    public void handleWrongContactExceptionAndConvertToSendMessage(WrongContactException ex) {
        convertAndSendMessage(ex);
    }

    @AfterThrowing(pointcut = "handledMethods()", throwing = "ex")
    public void handleUnexpectedActionExceptionAndConvertToSendMessage(UnexpectedActionException ex) {
        convertAndSendMessage(ex);
    }

    private void convertAndSendMessage(ProcessUpdateException ex) {
        log.debug(EXCEPTION_HANDLED, ex.getMessage());
        try {
            companyBot.execute(SendMessage.builder().chatId(ex.getChatId()).text(ex.getMessage()).build());
        } catch (TelegramApiException e) {
            log.error(SMTH_WENT_WRONG, ex.getChatId(), e.getMessage());
        }
    }

}
