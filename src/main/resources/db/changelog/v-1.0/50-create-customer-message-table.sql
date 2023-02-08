create table message (
       message_id  serial not null,
        message jsonb,
        inquiry_id int4,
        primary key (message_id)
    )

GO

alter table if exists message
       add constraint FK77vbe4kroqceidsx2srhdn22c
       foreign key (inquiry_id)
       references inquiry

GO