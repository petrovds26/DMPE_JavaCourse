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


CREATE TABLE IF NOT EXISTS billing (
                                       billing_id      BIGSERIAL       PRIMARY KEY,
                                       user_id         VARCHAR(255)    NOT NULL,
                                       operation_type  VARCHAR(50)     NOT NULL,
                                       machine_count   INTEGER         NOT NULL,
                                       parcel_count    INTEGER         NOT NULL,
                                       total_amount    DECIMAL(15,2)   NOT NULL,
                                       created_dt      TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP,
                                       modified_dt     TIMESTAMP       NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')::TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_billing_user_id ON billing(user_id);
CREATE INDEX IF NOT EXISTS idx_billing_created_dt ON billing(created_dt);
CREATE INDEX IF NOT EXISTS idx_billing_user_created ON billing(user_id, created_dt DESC);

COMMENT ON TABLE billing IS 'Таблица с записями биллинга (платежи за операции)';
COMMENT ON COLUMN billing.billing_id IS 'Первичный ключ';
COMMENT ON COLUMN billing.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN billing.operation_type IS 'Тип операции: LOAD или UNLOAD';
COMMENT ON COLUMN billing.machine_count IS 'Количество использованных машин';
COMMENT ON COLUMN billing.parcel_count IS 'Количество обработанных посылок';
COMMENT ON COLUMN billing.total_amount IS 'Сумма в рублях';
COMMENT ON COLUMN billing.created_dt IS 'Дата и время создания записи (UTC)';
COMMENT ON COLUMN billing.modified_dt IS 'Дата и время последнего обновления записи (UTC)';