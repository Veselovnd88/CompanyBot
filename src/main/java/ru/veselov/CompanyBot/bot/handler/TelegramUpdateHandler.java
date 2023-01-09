package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;

import java.util.Optional;

@Component
@Slf4j
public class TelegramUpdateHandler implements UpdateHandler {
    private final CommandHandler commandHandler;
    private final DepartmentCallbackHandler departmentCallbackHandler;
    private final InquiryMessageHandler inquiryMessageHandler;
    private final ContactCallbackHandler contactCallbackHandler;
    private final ContactMessageHandler contactMessageHandler;
    private final UserDataCache userDataCache;
    @Autowired
    public TelegramUpdateHandler(CommandHandler commandHandler, DepartmentCallbackHandler departmentCallbackHandler, InquiryMessageHandler inquiryMessageHandler, ContactCallbackHandler contactCallbackHandler, ContactMessageHandler contactMessageHandler, UserDataCache userDataCache) {
        this.commandHandler = commandHandler;
        this.departmentCallbackHandler = departmentCallbackHandler;
        this.inquiryMessageHandler = inquiryMessageHandler;
        this.contactCallbackHandler = contactCallbackHandler;
        this.contactMessageHandler = contactMessageHandler;
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {

        //TODO hasChatMember - если добавили в чат - то сохранить чат, сюда Sender будет отправлять сообщения + Mentions
        //по умолчанию - будет слать Админу
        if(update.hasMessage()&&isCommand(update)){
            return commandHandler.processUpdate(update);
        }
        if(update.hasMessage()){
            BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
            if(botState==BotState.AWAIT_MESSAGE){
                return inquiryMessageHandler.processUpdate(update);
            }
            if(botState==BotState.AWAIT_CONTACT){
                return contactMessageHandler.processUpdate(update);
            }
        }
        /* при передаче в чат или админу - проверять
        есть ли там контакт и делать соответствующую конвертацию
        и сразу отбивка в сервис по передаче в чат*/
        if(update.hasCallbackQuery()){
            BotState botState = userDataCache.getUserBotState(update.getCallbackQuery().getFrom().getId());
            if(botState==BotState.AWAIT_DEPARTMENT){
                return departmentCallbackHandler.processUpdate(update);
            }
            if(botState==BotState.AWAIT_MESSAGE||botState==BotState.AWAIT_SAVING){//при нажатии кнопки Ввести данные об обратной связи
                return contactCallbackHandler.processUpdate(update);
            }
        }

        return null;
    }


    private boolean isCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasEntities()) {
            Optional<MessageEntity> commandEntity = update.getMessage().getEntities()
                    .stream().filter(x -> "bot_command".equals(x.getType())).findFirst();
            return commandEntity.isPresent();
        }
        return false;
    }
}
