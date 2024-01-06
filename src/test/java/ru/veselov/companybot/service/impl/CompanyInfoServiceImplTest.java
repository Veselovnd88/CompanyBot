package ru.veselov.companybot.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.entity.CompanyInfoEntity;
import ru.veselov.companybot.repository.CompanyInfoRepository;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.util.TestUtils;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CompanyInfoServiceImplTest {

    @Mock
    CompanyInfoRepository companyInfoRepository;

    @InjectMocks
    CompanyInfoServiceImpl companyInfoService;

    @Captor
    ArgumentCaptor<CompanyInfoEntity> companyInfoEntityArgumentCaptor;

    @Test
    void save_saveInfoMessage() {
        Message infoMessage = TestUtils.getTextMessage("Text");

        companyInfoService.save(infoMessage);

        Mockito.verify(companyInfoRepository).save(companyInfoEntityArgumentCaptor.capture());
        CompanyInfoEntity captured = companyInfoEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getInfo()).as("Check if message identical to passed")
                .isEqualTo(infoMessage);
    }

    @Test
    void getLast_ifInfoMessageExists_returnInfoMessageFromRepo() {
        Message infoMessage = TestUtils.getTextMessage("info");
        CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
        companyInfoEntity.setInfo(infoMessage);
        Mockito.when(companyInfoRepository.findLast()).thenReturn(List.of(companyInfoEntity));

        Message last = companyInfoService.getLast();

        Assertions.assertThat(last.getText()).as("Check if last message if from DB").isEqualTo(infoMessage.getText());
        Mockito.verify(companyInfoRepository).findLast();
    }

    @Test
    void getLast_ifInfoMessageDoesntExists_returnStandardInfo() {
        Mockito.when(companyInfoRepository.findLast()).thenReturn(Collections.emptyList());

        Message last = companyInfoService.getLast();

        Assertions.assertThat(last.getText()).as("Check if last message is standard").isEqualTo(MessageUtils.BASE_INFO);
        Mockito.verify(companyInfoRepository).findLast();
    }

}