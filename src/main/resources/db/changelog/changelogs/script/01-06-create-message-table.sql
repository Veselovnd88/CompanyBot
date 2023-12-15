CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE message
(
    message_id INTEGER PRIMARY KEY,
    message    jsonb,
    inquiry_id uuid REFERENCES public.inquiry (inquiry_id)
)
    GO