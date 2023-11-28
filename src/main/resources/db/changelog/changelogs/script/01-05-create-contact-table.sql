CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE contact
(
    contact_id  uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    card        jsonb,
    email       VARCHAR(255),
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    phone       VARCHAR(255),
    second_name VARCHAR(255),
    customer_id int8 REFERENCES public.customer (id)
)
    GO