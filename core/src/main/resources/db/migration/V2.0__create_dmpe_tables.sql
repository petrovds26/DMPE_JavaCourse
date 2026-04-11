CREATE TABLE IF NOT EXISTS parcel (
                                      parcel_key    BIGSERIAL       PRIMARY KEY,
                                      name          VARCHAR(255)    NOT NULL,
                                      form          TEXT            NOT NULL,
                                      symbol        VARCHAR(1)      NOT NULL,
                                      created_dt    TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP,
                                      modified_dt   TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_parcel_name ON parcel(name);

COMMENT ON TABLE parcel IS 'Таблица с посылками';
COMMENT ON COLUMN parcel.parcel_key IS 'Первичный ключ';
COMMENT ON COLUMN parcel.name IS 'Название посылки (уникальное)';
COMMENT ON COLUMN parcel.form IS 'Форма посылки (многострочный текст)';
COMMENT ON COLUMN parcel.symbol IS 'Символ, которым заполнена посылка';
COMMENT ON COLUMN parcel.created_dt IS 'Дата и время создания записи (UTC)';
COMMENT ON COLUMN parcel.modified_dt IS 'Дата и время последнего обновления записи (UTC)';

CREATE TABLE IF NOT EXISTS billing_outbox (
                                  outbox_key    BIGSERIAL       PRIMARY KEY,
                                  payload       TEXT            NOT NULL,
                                  sent_dt       TIMESTAMP,
                                  created_dt    TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP,
                                  modified_dt   TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_billing_outbox_sent_dt ON billing_outbox(sent_dt);

COMMENT ON TABLE billing_outbox IS 'Таблица для паттерна Transactional Outbox - хранение событий перед отправкой в Kafka';
COMMENT ON COLUMN billing_outbox.outbox_key IS 'Уникальный идентификатор события';
COMMENT ON COLUMN billing_outbox.payload IS 'JSON с данными события (BillingDto)';
COMMENT ON COLUMN billing_outbox.sent_dt IS 'Дата отправки в Kafka (NULL - не отправлено)';
COMMENT ON COLUMN billing_outbox.created_dt IS 'Дата и время создания записи (UTC)';
COMMENT ON COLUMN billing_outbox.modified_dt IS 'Дата и время последнего обновления записи (UTC)';

CREATE TABLE IF NOT EXISTS shedlock
(
    name                        VARCHAR(64)         PRIMARY KEY,
    lock_until                  TIMESTAMP(3)        NULL,
    locked_at                   TIMESTAMP(3)        NULL,
    locked_by                   VARCHAR(255)        NULL
);