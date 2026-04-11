-- Очистка данных перед вставкой
DELETE FROM billing WHERE user_id = 'Dm';
DELETE FROM billing WHERE user_id = 'Olga';

-- Запись 1: попадает под фильтр (январь 2026)
INSERT INTO billing (external_id, user_id, operation_type, machine_count, parcel_count, total_amount, created_dt, modified_dt)
VALUES ('ext-test-001', 'Dm', 'LOAD', 2, 10, 5000.00, '2026-01-15 10:30:00', '2026-01-15 10:30:00');

-- Запись 2: попадает под фильтр (январь 2026)
INSERT INTO billing (external_id, user_id, operation_type, machine_count, parcel_count, total_amount, created_dt, modified_dt)
VALUES ('ext-test-002', 'Dm', 'UNLOAD', 1, 5, 3040.00, '2026-01-20 14:15:00', '2026-01-20 14:15:00');

-- Запись 3: НЕ попадает под фильтр (февраль 2026)
INSERT INTO billing (external_id, user_id, operation_type, machine_count, parcel_count, total_amount, created_dt, modified_dt)
VALUES ('ext-test-003', 'Dm', 'LOAD', 3, 15, 9000.00, '2026-02-10 09:45:00', '2026-02-10 09:45:00');

-- Дополнительные данные для других тестов
INSERT INTO billing (external_id, user_id, operation_type, machine_count, parcel_count, total_amount, created_dt, modified_dt)
VALUES ('ext-test-004', 'Olga', 'LOAD', 1, 3, 1500.00, '2026-01-25 12:00:00', '2026-01-25 12:00:00');