package ru.veselov.CompanyBot.model;

import org.aspectj.bridge.Message;

import java.util.LinkedList;
import java.util.List;

public class CustomerInquiry {
    private Department department;
    private final List<Message> messages= new LinkedList<>();
}
