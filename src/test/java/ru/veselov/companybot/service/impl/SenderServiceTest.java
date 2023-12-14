package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.veselov.companybot.bot.CompanyBot;
import ru.veselov.companybot.model.ContactModel;
import ru.veselov.companybot.model.InquiryModel;
import ru.veselov.companybot.service.MessagesToSendCreator;
import ru.veselov.companybot.util.TestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SenderServiceTest {

    @Mock
    CompanyBot bot;

    @Mock
    ChatServiceImpl chatService;

    @Mock
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Mock
    MessagesToSendCreator messagesToSendCreator;

    @InjectMocks
    SenderService senderService;

    @Captor
    ArgumentCaptor<Instant> instantArgumentCaptor;

    Long chatInterval = 600000L; //ten minutes

    Long chatId = 1000L;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(senderService, "adminId", TestUtils.ADMIN_ID.toString(), String.class);
        ReflectionTestUtils.setField(senderService, "chatInterval", chatInterval, Long.class);//10 min
        senderService.configure();
    }

    @Test
    void shouldSendMessagesTo10ChatsWithDelays() {
        List<Chat> chats = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Chat chat = new Chat();
            chat.setId(chatId);
            chat.setType("channel");
            chats.add(chat);
        }
        Mockito.when(chatService.findAll()).thenReturn(chats);
        InquiryModel inquiryModel = TestUtils.getInquiryModel();
        ContactModel contact = TestUtils.getUserContactModel();
        Mockito.when(messagesToSendCreator.createMessagesToSend(inquiryModel, contact, chatId.toString()))
                .thenReturn(List.of(
                        (PartialBotApiMethod<?>) new SendPhoto(),
                        (PartialBotApiMethod<?>) new SendMessage()
                ));

        senderService.send(inquiryModel, contact);

        Mockito.verify(threadPoolTaskScheduler, Mockito.times(8)).schedule(Mockito.any(),
                instantArgumentCaptor.capture());
        Mockito.verify(threadPoolTaskScheduler, Mockito.times(2)).execute(Mockito.any());

        List<Instant> allValues = instantArgumentCaptor.getAllValues();
        for (int i = 0; i < allValues.size(); i++) {
            boolean after = allValues.get(i).isAfter(Instant.now()
                    .plus((long) ((chatInterval * 0.9) * (i + 1)), ChronoUnit.MILLIS));//9 minutes
            Assertions.assertThat(after).isTrue();
        }
    }

    @Test
    void shouldSendMessageWithoutDelayIfLastSendWasBeforeInterval() {
        Map<Long, Instant> chatTimers = (Map<Long, Instant>) ReflectionTestUtils.getField(senderService, "chatTimers");
        assert chatTimers != null;
        chatTimers.put(chatId, Instant.now().minus((long) (chatInterval * 1.1), ChronoUnit.MILLIS));
        Chat chat = new Chat();
        chat.setId(1000L);
        chat.setType("channel");
        Mockito.when(chatService.findAll()).thenReturn(List.of(chat));
        InquiryModel inquiryModel = TestUtils.getInquiryModel();
        ContactModel contact = TestUtils.getUserContactModel();
        Mockito.when(messagesToSendCreator.createMessagesToSend(inquiryModel, contact, "1000"))
                .thenReturn(List.of(
                        (PartialBotApiMethod<?>) new SendPhoto(),
                        (PartialBotApiMethod<?>) new SendMessage()
                ));

        senderService.send(inquiryModel, contact);

        Mockito.verify(threadPoolTaskScheduler, Mockito.never()).schedule(Mockito.any(), Mockito.any(Instant.class));
        Mockito.verify(threadPoolTaskScheduler, Mockito.times(2)).execute(Mockito.any());
    }

}
