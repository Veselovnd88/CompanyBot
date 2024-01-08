package ru.veselov.companybot.exception.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.veselov.companybot.bot.keyboard.impl.ContactKeyboardHelperImpl;
import ru.veselov.companybot.exception.ContactProcessingException;
import ru.veselov.companybot.exception.CriticalBotException;
import ru.veselov.companybot.exception.MessageProcessingException;
import ru.veselov.companybot.exception.UnexpectedCallbackException;
import ru.veselov.companybot.exception.UnexpectedMessageException;
import ru.veselov.companybot.exception.WrongBotStateException;
import ru.veselov.companybot.exception.util.ExceptionMessageUtils;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BotExceptionHandler {

    private final ContactKeyboardHelperImpl contactKeyboardHelper;

    @Pointcut("@annotation(ru.veselov.companybot.exception.handler.BotExceptionToMessage)")
    public void handledMethods() {
    }

    /**
     * Around aspect for handling exceptions
     *
     * @param joinPoint method that we will try to proceed
     * @return Object as result of performing method
     * @throws CriticalBotException if something went wrong
     */
    @Around(value = "handledMethods()")
    public Object handleContactProcessingExceptionAndConvertToSendMessage(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (ContactProcessingException ex) {
            log.warn(ExceptionMessageUtils.HANDLED_EXCEPTION_WITH_MESSAGE,
                    ex.getClass().getSimpleName(), ex.getMessage());
            //return keyboard with saved/filled fields
            return SendMessage.builder().chatId(ex.getChatId())
                    .text(ex.getMessage())
                    .replyMarkup(contactKeyboardHelper.getCurrentContactKeyboard(Long.valueOf(ex.getChatId())))
                    .build();
        } catch (WrongBotStateException | MessageProcessingException | UnexpectedMessageException ex) {
            log.warn(ExceptionMessageUtils.HANDLED_EXCEPTION_WITH_MESSAGE,
                    ex.getClass().getSimpleName(), ex.getMessage());
            return SendMessage.builder().chatId(ex.getChatId()).text(ex.getMessage()).build();
        } catch (UnexpectedCallbackException ex) {
            log.warn(ExceptionMessageUtils.HANDLED_EXCEPTION_WITH_MESSAGE,
                    ex.getClass().getSimpleName(), ex.getMessage());
            return AnswerCallbackQuery.builder().callbackQueryId(ex.getChatId()).text(ex.getMessage()).build();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new CriticalBotException(ExceptionMessageUtils.SMTH_WENT_WRONG, ex);
        }
    }

}
