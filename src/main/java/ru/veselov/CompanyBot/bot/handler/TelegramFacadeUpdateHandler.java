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
import ru.veselov.CompanyBot.bot.HandlerContext;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactCallbackHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.ContactMessageHandler;
import ru.veselov.CompanyBot.bot.handler.inquiry.InquiryMessageHandler;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.util.BotAnswerUtil;

import java.util.Optional;

@Component
@Slf4j
public class TelegramFacadeUpdateHandler implements UpdateHandler {
    //Фасад
    @Value("${bot.adminId}")
    private String adminId;
    private final CommandHandler commandHandler;
    private final InquiryMessageHandler inquiryMessageHandler;
    private final ContactCallbackHandler contactCallbackHandler;
    private final ContactMessageHandler contactMessageHandler;
    private final ChannelConnectHandler channelConnectHandler;

    private final HandlerContext handlerContext;
    private final UserDataCache userDataCache;
    private final BotAnswerUtil botAnswerUtil;
    @Autowired
    public TelegramFacadeUpdateHandler(CommandHandler commandHandler,
                                       InquiryMessageHandler inquiryMessageHandler, ContactCallbackHandler contactCallbackHandler, ContactMessageHandler contactMessageHandler, ChannelConnectHandler channelConnectHandler, HandlerContext handlerContext, UserDataCache userDataCache, BotAnswerUtil botAnswerUtil) {
        this.commandHandler = commandHandler;
        this.inquiryMessageHandler = inquiryMessageHandler;
        this.contactCallbackHandler = contactCallbackHandler;
        this.contactMessageHandler = contactMessageHandler;
        this.channelConnectHandler = channelConnectHandler;
        this.handlerContext = handlerContext;
        this.userDataCache = userDataCache;
        this.botAnswerUtil = botAnswerUtil;
    }

    @Override
    public synchronized BotApiMethod<?> processUpdate(Update update) {
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
            if(handlerContext.isInMessageContext(botState)){
                return handlerContext.getMessageHandler(botState).processUpdate(update);
            }
            return botAnswerUtil.getAnswerNotSupportMessage(update.getMessage().getFrom().getId().toString());
        }

        if(update.hasCallbackQuery()){
            BotState botState = userDataCache.getUserBotState(update.getCallbackQuery().getFrom().getId());
            if(handlerContext.isInCallbackContext(botState)){
                return handlerContext.getCallbackHandler(botState).processUpdate(update);
            }
            return botAnswerUtil.getAnswerCallbackAnotherAction(update.getCallbackQuery().getId());
        }

        return null;
    }


    private boolean isCommand(Update update) {
        /*additional checking if message is not forwarded*/
        if (update.hasMessage() && update.getMessage().hasEntities()&&update.getMessage().getForwardFrom()==null) {
            Optional<MessageEntity> commandEntity = update.getMessage().getEntities()
                    .stream().filter(x -> "bot_command".equals(x.getType())).findFirst();
            return commandEntity.isPresent();
        }
        return false;
    }


}
