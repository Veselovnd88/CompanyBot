create table manager_division (
        manager_id int8 not null,
        division_id varchar(255) not null,
        primary key (manager_id, division_id)
    )

GO

alter table if exists manager_division
       add constraint FKf9bmvrjliatpekesgy6t62u4b
       foreign key (division_id)
       references division

GO