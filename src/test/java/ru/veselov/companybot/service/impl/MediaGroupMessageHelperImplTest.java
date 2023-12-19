package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.service.sender.impl.MediaGroupMessageHelperImpl;
import ru.veselov.companybot.util.TestUtils;

import java.util.LinkedHashMap;
import java.util.Map;

class MediaGroupMessageHelperImplTest {

    @Test
    void shouldProcessMessagesAndReturnGroupsOneGroupInEachMessage() {
        Map<Integer, Message> mediaGroupMessages = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            Message messageWithGroupAndPhoto = TestUtils
                    .getMessageWithGroupAndPhoto(String.valueOf(1000 + i), String.valueOf(i));
            mediaGroupMessages.put(i, messageWithGroupAndPhoto);
        }
        MediaGroupMessageHelperImpl mediaGroupHelper = new MediaGroupMessageHelperImpl();

        Map<Integer, SendMediaGroup> sendMediaGroups = mediaGroupHelper
                .convertMediaGroupMessages(mediaGroupMessages, TestUtils.CHAT_ID.toString());

        Assertions.assertThat(sendMediaGroups).hasSize(10);
        Assertions.assertThat(sendMediaGroups.get(4).getMedias()).hasSize(1);
        Assertions.assertThat(sendMediaGroups.get(4).getMedias().get(0).getType()).isEqualTo("photo");
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
            Message messageWithGroupAndPhoto = TestUtils
                    .getMessageWithGroupAndPhoto(mediaGroupId, String.valueOf(i));
            mediaGroupMessages.put(i, messageWithGroupAndPhoto);
        }
        MediaGroupMessageHelperImpl mediaGroupHelper = new MediaGroupMessageHelperImpl();

        Map<Integer, SendMediaGroup> sendMediaGroups = mediaGroupHelper
                .convertMediaGroupMessages(mediaGroupMessages, TestUtils.CHAT_ID.toString());

        Assertions.assertThat(sendMediaGroups).hasSize(2);
        Assertions.assertThat(sendMediaGroups.get(0).getMedias()).hasSize(5);
        Assertions.assertThat(sendMediaGroups.get(0).getMedias().get(0).getType()).isEqualTo("photo");
    }

}