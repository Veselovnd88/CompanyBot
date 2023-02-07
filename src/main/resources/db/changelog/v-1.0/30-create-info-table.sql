create table company_info (
       id  serial not null,
        changed_at timestamp,
        info jsonb,
        primary key (id)
    )

GO

