package ru.veselov.CompanyBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.CompanyBot.dao.CustomerDAO;
import ru.veselov.CompanyBot.dao.InquiryDAO;
import ru.veselov.CompanyBot.entity.Customer;
import ru.veselov.CompanyBot.entity.CustomerMessageEntity;
import ru.veselov.CompanyBot.entity.Inquiry;
import ru.veselov.CompanyBot.model.CustomerInquiry;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InquiryService {
    private final InquiryDAO inquiryDAO;
    private final CustomerDAO customerDAO;
    @Autowired
    public InquiryService(InquiryDAO inquiryDAO, CustomerDAO customerDAO) {
        this.inquiryDAO = inquiryDAO;
        this.customerDAO = customerDAO;
    }

    public Inquiry save(CustomerInquiry inquiry){
        Optional<Customer> customerEntity = customerDAO.findOne(inquiry.getUserId());
        if(customerEntity.isPresent()){
            Inquiry inquiryEntity = toInquiryEntity(inquiry);
            inquiryEntity.setCustomer(customerEntity.get());
            return inquiryDAO.save(inquiryEntity);
        }
        else return null;
    }
    public Optional<Inquiry> findWithMessages(Integer id){
        return inquiryDAO.findOneWithMessages(id);
    }

    public List<Inquiry> findAll(){
        return inquiryDAO.findAll();
    }

    private Inquiry toInquiryEntity(CustomerInquiry customerInquiry){
        Inquiry inquiry = new Inquiry();
        inquiry.setDepartment(customerInquiry.getDepartment());
        inquiry.setDate(new Date());
        for(Message message: customerInquiry.getMessages()){
            CustomerMessageEntity cme = new CustomerMessageEntity();
            cme.setMessage(message);
            inquiry.addMessage(cme);
        }
        return inquiry;
    }
}
