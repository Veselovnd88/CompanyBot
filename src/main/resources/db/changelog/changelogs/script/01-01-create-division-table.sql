CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE division
(
    division_id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(10)  NOT NULL UNIQUE,
    description VARCHAR(45) NOT NULL
)