CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE message
(
    message_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    message    jsonb,
    inquiry_id uuid REFERENCES public.inquiry (inquiry_id)
)
    GO