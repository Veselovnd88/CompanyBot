package ru.veselov.CompanyBot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.CompanyBot.bot.CompanyBot;
import ru.veselov.CompanyBot.model.ContactModel;
import ru.veselov.CompanyBot.service.Sender;

@Slf4j
@Component
public class ContactSender implements Sender {
    private ContactModel contact;
    private boolean hasInquiry;

    public void setUpContactSender(ContactModel contactModel, boolean hasInquiry){
        this.contact = contactModel;
        this.hasInquiry=hasInquiry;
    }
    @Override
    public void send(CompanyBot bot, Chat chat) throws TelegramApiException {
        log.info("{}: в чат отправляются контактные данные", chat.getId());
        SendMessage contactMessage = createContactMessage(contact, chat.getId(), hasInquiry);
        bot.execute(contactMessage);
        if(contact.getContact()!=null){
            SendContact sendContact = new SendContact();
            sendContact.setChatId(chat.getId());
            sendContact.setLastName(contact.getContact().getLastName());
            sendContact.setFirstName(contact.getContact().getFirstName());
            sendContact.setVCard(contact.getContact().getVCard());
            sendContact.setPhoneNumber(contact.getContact().getPhoneNumber());
            bot.execute(sendContact);
        }
        contact=null;
    }

    private SendMessage createContactMessage(ContactModel contact, Long chatId, boolean hasInquiry){
        StringBuilder sb = new StringBuilder();
        if(contact.getLastName()!=null){
            sb.append(contact.getLastName()).append(" ");
        }
        if(contact.getFirstName()!=null){
            sb.append(contact.getFirstName()).append(" ");
        }
        if(contact.getSecondName()!=null){
            sb.append(contact.getSecondName()).append(" ");
        }
        if(contact.getPhone()!=null){
            sb.append("\nТелефон: ").append(contact.getPhone());
        }
        if(contact.getEmail()!=null){
            sb.append("\nЭл. почта: ").append(contact.getEmail());
        }
        String prefix;
        if(hasInquiry){
            prefix = "Контактное лицо для связи: ";
        }
        else{
            prefix="Направлена заявка на обратный звонок\nКонтактное лицо для связи: ";
        }
        return SendMessage.builder().chatId(chatId)
                .text((prefix+
                        "\n"+ sb).trim()).build();
    }

    @Profile("test")
    public SendMessage getContactMessage(ContactModel contact, Long chatId){
        return createContactMessage(contact, chatId, true);
    }

}
