package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TelegramUpdateHandler implements UpdateHandler {
    @Value("${bot.adminId}")
    private String adminId;
    private final CommandHandler commandHandler;
    private final DepartmentCallbackHandler departmentCallbackHandler;
    private final InquiryMessageHandler inquiryMessageHandler;
    private final ContactCallbackHandler contactCallbackHandler;
    private final ContactMessageHandler contactMessageHandler;
    private final ChannelConnectHandler channelConnectHandler;
    private final UserDataCache userDataCache;
    @Autowired
    public TelegramUpdateHandler(CommandHandler commandHandler,
                                 DepartmentCallbackHandler departmentCallbackHandler,
                                 InquiryMessageHandler inquiryMessageHandler, ContactCallbackHandler contactCallbackHandler, ContactMessageHandler contactMessageHandler, ChannelConnectHandler channelConnectHandler, UserDataCache userDataCache) {
        this.commandHandler = commandHandler;
        this.departmentCallbackHandler = departmentCallbackHandler;
        this.inquiryMessageHandler = inquiryMessageHandler;
        this.contactCallbackHandler = contactCallbackHandler;
        this.contactMessageHandler = contactMessageHandler;
        this.channelConnectHandler = channelConnectHandler;
        this.userDataCache = userDataCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        //Обработка апдейтов, связанных с присоединением бота к чату
        if(update.hasMyChatMember()){
            if(update.getMyChatMember().getFrom().getId().toString().equals(adminId)){
                return channelConnectHandler.processUpdate(update);
            }
            else{
                return SendMessage.builder().chatId(update.getMyChatMember().getFrom().getId())
                        .text("Я работаю только в тех каналах, куда меня добавил администратор")
                        .build();
            }
        }


        if(update.hasMessage()&&isCommand(update)){
            return commandHandler.processUpdate(update);
        }
        if(update.hasMessage()){
            BotState botState = userDataCache.getUserBotState(update.getMessage().getFrom().getId());
            if(botState==BotState.AWAIT_MESSAGE){
                return inquiryMessageHandler.processUpdate(update);
            }
            if(isContactInputState(botState)){
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
            if(isContactInputCallbackState(botState)){//при нажатии кнопки Ввести данные об обратной связи
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

    private boolean isContactInputState(BotState botState){
        List<BotState> states = List.of(BotState.AWAIT_NAME,BotState.AWAIT_SHARED,BotState.AWAIT_PHONE,
                BotState.AWAIT_EMAIL,BotState.AWAIT_CONTACT);
        return states.contains(botState);
    }

    private boolean isContactInputCallbackState(BotState botState){
        List<BotState> states = List.of(
                BotState.AWAIT_NAME,BotState.AWAIT_SHARED,BotState.AWAIT_PHONE,BotState.AWAIT_EMAIL,
                BotState.AWAIT_CONTACT,BotState.AWAIT_MESSAGE,BotState.AWAIT_SAVING
        );
        return states.contains(botState);
    }
}
