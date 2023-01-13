package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.BotState;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.ContactCache;
import ru.veselov.CompanyBot.cache.UserDataCache;
import ru.veselov.CompanyBot.model.CustomerContact;
import ru.veselov.CompanyBot.util.KeyBoardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ContactMessageHandler implements UpdateHandler {
    private final UserDataCache userDataCache;
    private final ContactCache contactCache;
    private final KeyBoardUtils keyBoardUtils;
    private final EmailValidator emailValidator;
    @Autowired
    public ContactMessageHandler(UserDataCache userDataCache, ContactCache contactCache, KeyBoardUtils keyBoardUtils, EmailValidator emailValidator) {
        this.userDataCache = userDataCache;
        this.contactCache = contactCache;
        this.keyBoardUtils = keyBoardUtils;
        this.emailValidator = emailValidator;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCache.getUserBotState(userId);
        CustomerContact contact = contactCache.getContact(userId);
        if(update.getMessage().hasText()){
            String text = update.getMessage().getText();
            switch (botState){
                case AWAIT_NAME :
                    userDataCache.setUserBotState(userId,BotState.AWAIT_CONTACT);
                    return processName(contact,text);
                case AWAIT_PHONE:
                    userDataCache.setUserBotState(userId,BotState.AWAIT_CONTACT);
                    return processPhone(contact,text);
                case AWAIT_EMAIL:
                    userDataCache.setUserBotState(userId,BotState.AWAIT_CONTACT);
                    return processEmail(contact,text);
            }
        }

        if(update.getMessage().hasContact()){
            Contact messageContact = update.getMessage().getContact();
            contact.setContact(messageContact);
            if(contact.getFirstName()==null){
                contact.setFirstName(messageContact.getFirstName());
            }
            if(contact.getLastName()==null){
                contact.setLastName(messageContact.getLastName());
            }
            if(contact.getPhone()==null){
                contact.setPhone(messageContact.getPhoneNumber());
            }
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.INPUT_CONTACT).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }

        return SendMessage.builder().chatId(userId).text(MessageUtils.WRONG_CONTACT_FORMAT).build();
    }


    private BotApiMethod<?> processName(CustomerContact contact, String name, Update update){
        if(name.length()>250){
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.NAME_TOO_LONG).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
        String[] s = name.split(" ");
        if(s.length==0){
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_NAME_FORMAT).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
        contact.setLastName(s[0]);
        if(s.length>1){
            contact.setFirstName(s[1]);
        }
        if(s.length>2){
            StringBuilder sb=new StringBuilder();
            for(int i=2; i<s.length;i++){
                sb.append(s[i]).append(" ");
            }
            contact.setSecondName(sb.toString());
        }
        return keyBoardUtils.editMessageSavedField(update,"name");//FIXME - айди сообщения тут забирается по другому
    }

    private SendMessage processPhone(CustomerContact contact, String phone){
        Pattern pattern = Pattern.compile("^[+]?[-. 0-9{}]{11,18}$");
        Matcher matcher = pattern.matcher(phone);
        if(matcher.matches()){
            contact.setPhone(phone);
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.INPUT_CONTACT).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
        else {
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_PHONE).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
    }

    private SendMessage processEmail(CustomerContact contact, String email){
        if(emailValidator.isValid(email,null)){
            contact.setEmail(email);
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.INPUT_CONTACT)
                    .replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }else{
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_EMAIL)
                    .replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
    }


}
