package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.mapper.impl.SendMediaMapperImpl;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.sender.impl.InquiryMessageCreatorImpl;
import ru.veselov.companybot.service.sender.impl.MediaGroupMessageHelperImpl;
import ru.veselov.companybot.service.sender.impl.SimpleMessageMediaHelperImpl;
import ru.veselov.companybot.util.TestUtils;

import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InquiryMessageCreatorImplTest {

    InquiryMessageCreatorImpl inquiryMessageCreator = new InquiryMessageCreatorImpl(
            new SimpleMessageMediaHelperImpl(new SendMediaMapperImpl()),
            new MediaGroupMessageHelperImpl());

    String chatId = TestUtils.CHAT_ID.toString();

    InquiryModel inquiryModel = new InquiryModel(TestUtils.USER_ID, TestUtils.getDivision());

    @Test
    void shouldReturnFiveMessagesToSend() {
        List<Message> messages = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            String mediaGroupId;
            if (i < 5) {
                mediaGroupId = "1000";
            } else {
                mediaGroupId = "1001";
            }
            Message messageWithGroupAndPhoto = TestUtils.getMessageWithGroupAndPhoto(mediaGroupId, String.valueOf(i));
            messages.add(messageWithGroupAndPhoto);//here 10 messages that will be compact to 2 media groups
        }
        messages.add(TestUtils.getPhotoMessage());
        messages.add(TestUtils.getTextMessage("one"));
        messages.add(TestUtils.getTextMessage("two"));
        inquiryModel.setMessages(messages);

        List<PartialBotApiMethod<?>> botMessagesToSend = inquiryMessageCreator
                .createBotMessagesToSend(inquiryModel, chatId);

        Assertions.assertThat(botMessagesToSend).hasSize(6);
        Assertions.assertThat(botMessagesToSend.get(1)).isInstanceOf(SendMediaGroup.class);
    }

    @Test
    void shouldReturn16MessagesToSend() {
        //generate 10 messages, with 5 groups
        Integer j = 0;
        List<Message> messages = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            String mediaGroupId;
            if (i % 2 == 0) {
                mediaGroupId = String.valueOf(j);
            } else {
                mediaGroupId = String.valueOf(j);
                j++;
            }
            Message messageWithGroupAndPhoto = TestUtils.getMessageWithGroupAndPhoto(mediaGroupId, String.valueOf(j));
            messages.add(messageWithGroupAndPhoto);//one message for group
            messages.add(TestUtils.getTextMessage("text " + i));//one simple text message
        }
        //BASE G0 T0 G0 T1 G1 T2 G1 T3 G2 T4 G2 T5 G3 T6 G3 T7 G4 T8 G4 T9
        Assertions.assertThat(messages).hasSize(20);
        inquiryModel.setMessages(messages);

        List<PartialBotApiMethod<?>> botMessagesToSend = inquiryMessageCreator
                .createBotMessagesToSend(inquiryModel, chatId);
        //BASE G0 T0 T1 G1 T2 T3 G2 T4 T5 G3 T6 T7 G4 T8 T9
        Assertions.assertThat(botMessagesToSend).hasSize(16);
        Assertions.assertThat(botMessagesToSend.get(1)).isInstanceOf(SendMediaGroup.class);
        Assertions.assertThat(botMessagesToSend.get(2)).isInstanceOf(SendMessage.class);
        SendMessage firstInquiryMessage = (SendMessage) botMessagesToSend.get(2);
        Assertions.assertThat(firstInquiryMessage.getText()).isEqualTo("text 0");

        SendMediaGroup firstSendMediaGroup = (SendMediaGroup) botMessagesToSend.get(1);
        Assertions.assertThat(firstSendMediaGroup.getMedias().get(0).getMedia()).isEqualTo(String.valueOf(0));

        Assertions.assertThat(botMessagesToSend.get(13)).isInstanceOf(SendMediaGroup.class);
        Assertions.assertThat(botMessagesToSend.get(15)).isInstanceOf(SendMessage.class);
        SendMessage lastInquiryMessage = (SendMessage) botMessagesToSend.get(15);
        Assertions.assertThat(lastInquiryMessage.getText()).isEqualTo("text 9");

        SendMediaGroup lastSendMediaGroup = (SendMediaGroup) botMessagesToSend.get(13);
        Assertions.assertThat(lastSendMediaGroup.getMedias().get(0).getMedia()).isEqualTo(String.valueOf(4));
    }


}