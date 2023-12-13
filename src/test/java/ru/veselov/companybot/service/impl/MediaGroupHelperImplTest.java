package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.util.TestUtils;

import java.util.LinkedHashMap;
import java.util.Map;

class MediaGroupHelperImplTest {

    @Test
    void shouldProcessMessagesAndReturnGroupsOneGroupInEachMessage() {
        Map<Integer, Message> mediaGroupMessages = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            Message messageWithGroupAndPhoto = TestUtils.getMessageWithGroupAndPhoto(String.valueOf(1000 + i));
            mediaGroupMessages.put(i, messageWithGroupAndPhoto);
        }

        MediaGroupHelperImpl mediaGroupHelper = new MediaGroupHelperImpl();
        Map<Integer, SendMediaGroup> sendMediaGroups = mediaGroupHelper
                .convertMediaGroupMessages(mediaGroupMessages, TestUtils.CHAT_ID.toString());

        System.out.println(sendMediaGroups);

        Assertions.assertThat(sendMediaGroups).hasSize(10);
        Assertions.assertThat(sendMediaGroups.get(4).getMedias()).hasSize(1);
    }

    @Test
    void shouldProcessMessagesAndReturnGroupsTwoGroups() {
        Map<Integer, Message> mediaGroupMessages = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            String mediaGroupId;
            if (i < 5) {
                mediaGroupId = "1000";
            } else {
                mediaGroupId = "1001";
            }
            Message messageWithGroupAndPhoto = TestUtils.getMessageWithGroupAndPhoto(mediaGroupId);
            mediaGroupMessages.put(i, messageWithGroupAndPhoto);
        }

        MediaGroupHelperImpl mediaGroupHelper = new MediaGroupHelperImpl();
        Map<Integer, SendMediaGroup> sendMediaGroups = mediaGroupHelper
                .convertMediaGroupMessages(mediaGroupMessages, TestUtils.CHAT_ID.toString());

        System.out.println(sendMediaGroups);

        Assertions.assertThat(sendMediaGroups).hasSize(2);
        Assertions.assertThat(sendMediaGroups.get(0).getMedias()).hasSize(5);
    }

}