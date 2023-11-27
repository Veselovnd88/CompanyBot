package ru.veselov.CompanyBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.dao.CompanyInfoRepository;
import ru.veselov.CompanyBot.entity.CompanyInfoEntity;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyInfoService {
    private final CompanyInfoRepository companyInfoRepository;
    @Autowired
    public CompanyInfoService(CompanyInfoRepository companyInfoRepository) {
        this.companyInfoRepository = companyInfoRepository;
    }



    public void save(Message message){
        companyInfoRepository.save(toEntity(message));
    }

    public Message getLast(){
        List<CompanyInfoEntity> last = companyInfoRepository.findLast();
        Message message;
        if(last.size()==0){
            message=new Message();
            message.setText("Информация о компании еще не установлена");
        }
        else{
            message=toMessage(last.get(0));
        }
        MessageUtils.about=message;
        return message;
    }

    private CompanyInfoEntity toEntity(Message message){
        CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
        companyInfoEntity.setInfo(message);
        companyInfoEntity.setChangedAt(new Date());
        return companyInfoEntity;
    }

    private Message toMessage(CompanyInfoEntity companyInfoEntity){
        Message message = new Message();
        message.setText(companyInfoEntity.getInfo().getText());
        message.setEntities(companyInfoEntity.getInfo().getEntities());
        return message;
    }

}
