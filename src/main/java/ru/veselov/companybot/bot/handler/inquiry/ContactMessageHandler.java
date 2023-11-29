package ru.veselov.companybot.bot.handler.inquiry;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.UpdateHandler;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.UserDataCache;
import ru.veselov.companybot.exception.WrongContactException;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.util.KeyBoardUtils;
import ru.veselov.companybot.util.MessageUtils;

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
    public BotApiMethod<?> processUpdate(Update update) throws WrongContactException {
        Long userId = update.getMessage().getFrom().getId();
        BotState botState = userDataCache.getUserBotState(userId);
        ContactModel contact = contactCache.getContact(userId);
        if(update.getMessage().hasText()){
            String text = update.getMessage().getText();
            switch (botState){
                case AWAIT_NAME :
                    return processName(contact,text);
                case AWAIT_PHONE:
                    return processPhone(contact,text);
                case AWAIT_EMAIL:
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
            userDataCache.setUserBotState(contact.getUserId(),BotState.AWAIT_CONTACT);
            return keyBoardUtils.editMessageSavedField(contact.getUserId(), "shared");
        }
        //Сюда придет если попало неправильное значение
        log.info("{}: неправильный формат контакта", update.getMessage().getFrom().getId());
        throw new WrongContactException(MessageUtils.WRONG_CONTACT_FORMAT,userId.toString());
    }

    private BotApiMethod<?> processName(ContactModel contact, String name){
        if(name.length()>250){
            log.info("{}: Попытка ввести имя более 250 знаков", contact.getUserId());
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.NAME_TOO_LONG).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
        String[] s = name.split(" ");
        if(s.length==0||name.length()==0){
            log.info("{}: введена пустая строка",contact.getUserId());
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_NAME_FORMAT).replyMarkup(keyBoardUtils.contactKeyBoard())
                    .build();
        }
        contact.setLastName(s[0].trim());
        if(s.length>1){
            contact.setFirstName(s[1].trim());
        }
        if(s.length>2){
            StringBuilder sb=new StringBuilder();
            for(int i=2; i<s.length;i++){
                sb.append(s[i]).append(" ");
            }
            contact.setSecondName(sb.toString().trim());
        }
        log.info("{}: добавлено имя в контакт {}", contact.getUserId(), name);
        userDataCache.setUserBotState(contact.getUserId(),BotState.AWAIT_CONTACT);
        return keyBoardUtils.editMessageSavedField(contact.getUserId(),"name");
    }

    private BotApiMethod<?> processPhone(ContactModel contact, String phone){
        Pattern pattern = Pattern.compile("^[+]?[-. 0-9{}]{11,18}$");
        Matcher matcher = pattern.matcher(phone);
        if(matcher.matches()){
            contact.setPhone(phone);
            log.info("{}: установлен телефонный номер {}", contact.getUserId(), phone);
            userDataCache.setUserBotState(contact.getUserId(),BotState.AWAIT_CONTACT);
            return keyBoardUtils.editMessageSavedField(contact.getUserId(), "phone");
        }
        else {
            log.info("{}: не корректный телефонный номер", contact.getUserId());
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_PHONE)
                    .build();
        }
    }

    private BotApiMethod<?> processEmail(ContactModel contact, String email){
        if(emailValidator.isValid(email,null)){
            contact.setEmail(email);
            log.info("{}: email добавлен {}",contact.getUserId(),email);
            userDataCache.setUserBotState(contact.getUserId(),BotState.AWAIT_CONTACT);
            return keyBoardUtils.editMessageSavedField(contact.getUserId(), "email");
        }else{
            log.info("{}: Некорректный ввод email",contact.getUserId());
            return SendMessage.builder().chatId(contact.getUserId())
                    .text(MessageUtils.WRONG_EMAIL)
                    .build();
        }
    }

    @Profile("test")
    public void getProcessedName(ContactModel contact, String name){
        processName(contact, name);
    }

}
