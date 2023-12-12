package ru.veselov.companybot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.service.Sender;

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

        bot.execute(contactMessage);

        contact=null;
    }



}
