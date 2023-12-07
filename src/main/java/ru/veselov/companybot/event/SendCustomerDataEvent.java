package ru.veselov.companybot.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendCustomerDataEvent {

    private InquiryModel inquiry;

    private ContactModel contact;

}
