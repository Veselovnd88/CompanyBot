CREATE TABLE inquiry
(
    inquiry_id  serial PRIMARY KEY,
    DATE        timestamp,
    customer_id int8 REFERENCES public.customer (id),
    division_id VARCHAR(255)
)
    GO
