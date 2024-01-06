package ru.veselov.companybot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.entity.CompanyInfoEntity;
import ru.veselov.companybot.repository.CompanyInfoRepository;
import ru.veselov.companybot.service.CompanyInfoService;
import ru.veselov.companybot.util.MessageUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;

    @Override
    @Transactional
    public Message save(Message message) {
        CompanyInfoEntity saved = companyInfoRepository.save(toEntity(message));
        log.info("Company info saved to db");
        return toMessage(saved);
    }

    @Override
    public Message getLast() {
        List<CompanyInfoEntity> last = companyInfoRepository.findLast();
        Message message;
        if (last.isEmpty()) {
            message = new Message();
            message.setText(MessageUtils.BASE_INFO);
        } else {
            message = toMessage(last.get(0));
        }
        log.debug("Retrieved last record for company info");
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
