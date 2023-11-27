package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.dao.CustomerRepository;
import ru.veselov.CompanyBot.dao.DivisionRepository;
import ru.veselov.CompanyBot.dao.InquiryRepository;
import ru.veselov.CompanyBot.entity.CustomerEntity;
import ru.veselov.CompanyBot.entity.CustomerMessageEntity;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.entity.Inquiry;
import ru.veselov.CompanyBot.model.InquiryModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final CustomerRepository customerRepository;
    private final DivisionRepository divisionRepository;
    @Autowired
    public InquiryService(InquiryRepository inquiryRepository, CustomerRepository customerRepository, DivisionRepository divisionRepository) {
        this.inquiryRepository = inquiryRepository;
        this.customerRepository = customerRepository;
        this.divisionRepository = divisionRepository;
    }

    public Inquiry save(InquiryModel inquiry){
        Optional<CustomerEntity> customerEntity = customerRepository.findOne(inquiry.getUserId());
        Optional<Division> divisionOptional = divisionRepository.findOne(inquiry.getDivision().getDivisionId());
        if(customerEntity.isPresent()&&divisionOptional.isPresent()){
            Inquiry inquiryEntity = toInquiryEntity(inquiry);
            inquiryEntity.setCustomerEntity(customerEntity.get());
            inquiryEntity.setDivision(divisionOptional.get());
            log.info("{}: запрос пользователя сохранен в БД",inquiry.getUserId());
            return inquiryRepository.save(inquiryEntity);
        }

        else{
            log.info("{}: запрос не сохранен, т.к. в бд нет клиента или отдела",inquiry.getUserId());
            return null;}
    }
    public Optional<Inquiry> findWithMessages(Integer id){
        return inquiryRepository.findOneWithMessages(id);
    }

    public List<Inquiry> findAll(){
        return inquiryRepository.findAll();
    }

    private Inquiry toInquiryEntity(InquiryModel inquiryModel){
        Inquiry inquiry = new Inquiry();
        inquiry.setDate(new Date());
        for(Message message: inquiryModel.getMessages()){
            CustomerMessageEntity cme = new CustomerMessageEntity();
            cme.setMessage(message);
            inquiry.addMessage(cme);
        }
        return inquiry;
    }
}
