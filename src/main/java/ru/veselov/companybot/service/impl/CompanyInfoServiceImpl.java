package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.util.MessageUtils;
import ru.veselov.companybot.entity.CompanyInfoEntity;
import ru.veselov.companybot.repository.CompanyInfoRepository;
import ru.veselov.companybot.service.CompanyInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;

    @Override
    @Transactional
    public void save(Message message) {
        companyInfoRepository.save(toEntity(message));
    }

    @Override
    public Message getLast() {
        List<CompanyInfoEntity> last = companyInfoRepository.findLast();
        Message message;
        if (last.isEmpty()) {
            message = new Message();
            message.setText("Информация о компании еще не установлена");
        } else {
            message = toMessage(last.get(0));
        }
        MessageUtils.setAbout(message);
        return message;
    }

    private CompanyInfoEntity toEntity(Message message) {
        CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
        companyInfoEntity.setInfo(message);
        return companyInfoEntity;
    }

    private Message toMessage(CompanyInfoEntity companyInfoEntity) {
        Message message = new Message();
        message.setText(companyInfoEntity.getInfo().getText());
        message.setEntities(companyInfoEntity.getInfo().getEntities());
        return message;
    }

}
