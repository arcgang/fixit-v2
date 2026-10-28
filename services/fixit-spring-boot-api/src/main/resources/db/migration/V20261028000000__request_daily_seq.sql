CREATE TABLE request_daily_sequence (
    seq_date    DATE    NOT NULL,
    last_value  INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_request_daily_sequence PRIMARY KEY (seq_date)
);
