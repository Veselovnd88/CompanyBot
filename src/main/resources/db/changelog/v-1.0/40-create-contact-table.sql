create table contact (
       contact_id  serial not null,
        card jsonb,
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        phone varchar(255),
        second_name varchar(255),
        customer_id int8,
        primary key (contact_id)
    )
GO

alter table if exists contact
       add constraint FKckoarj5a5jmet3b3smgdhaopw
       foreign key (customer_id)
       references customer
GO

