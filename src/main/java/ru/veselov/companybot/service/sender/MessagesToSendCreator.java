package ru.veselov.companybot.service.sender;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;

import java.util.List;

public interface MessagesToSendCreator {

    List<PartialBotApiMethod<?>> createMessagesToSend(InquiryModel inquiry, ContactModel contact, String chatId);

}
