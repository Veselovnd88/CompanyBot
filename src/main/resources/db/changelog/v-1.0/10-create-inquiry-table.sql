create table inquiry (
       inquiry_id  serial not null,
        date timestamp,
        customer_id int8,
        division_id varchar(255),
        primary key (inquiry_id)
    )
GO

alter table if exists inquiry
       add constraint FK9q9qboj7w0w2hp1wwdjna08qp
       foreign key (customer_id)
       references customer
GO