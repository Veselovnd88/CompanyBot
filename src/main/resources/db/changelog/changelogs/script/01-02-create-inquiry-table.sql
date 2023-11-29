CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE inquiry
(
    inquiry_id  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    DATE        timestamp,
    customer_id int8 REFERENCES public.customer (id),
    division_id UUID REFERENCES public.division (division_id)
)
    GO
