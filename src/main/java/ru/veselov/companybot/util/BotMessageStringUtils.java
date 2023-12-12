package ru.veselov.companybot.util;

import ru.veselov.companybot.model.ContactModel;

public class BotMessageStringUtils {

    public static String createContactMessage(ContactModel contact, boolean hasInquiry) {
        StringBuilder sb = new StringBuilder();
        sb.append(contact.getLastName() == null ? "" : (contact.getLastName() + " "));
        /*if (contact.getLastName() != null) {
            sb.append(contact.getLastName()).append(" ");
        }*/
        if (contact.getFirstName() != null) {
            sb.append(contact.getFirstName()).append(" ");
        }
        sb.append(contact.getFirstName() == null ? "" : (contact.getFirstName() + " "));
        /*if (contact.getSecondName() != null) {
            sb.append(contact.getSecondName()).append(" ");
        }*/
        sb.append(contact.getLastName() == null ? "" : ("\nТелефон: " + contact.getLastName()));
        /*if (contact.getPhone() != null) {
            sb.append("\nТелефон: ").append(contact.getPhone());
        }*/
        sb.append(contact.getEmail() == null ? "" : ("\nЭл. почта: " + contact.getEmail()));
        /*if (contact.getEmail() != null) {
            sb.append("\nЭл. почта: ").append(contact.getEmail());
        }*/
        String prefix;
        if (hasInquiry) {
            prefix = "Контактное лицо для связи: ";
        } else {
            prefix = "Направлена заявка на обратный звонок\nКонтактное лицо для связи: ";
        }
        return prefix + sb.toString().trim();
    }

    private BotMessageStringUtils() {
    }
}
